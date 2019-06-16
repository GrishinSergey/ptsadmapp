package com.sagrishin.ptsadmapp.domain.services.impl

import com.sagrishin.ptsadmapp.core.database.Appointment
import com.sagrishin.ptsadmapp.core.database.Appointments
import com.sagrishin.ptsadmapp.core.datetime.mapByDay
import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainDay
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import com.sagrishin.ptsadmapp.domain.services.AppointmentsService
import com.sagrishin.ptsadmapp.domain.services.PatientsService
import com.sagrishin.ptsadmapp.domain.services.toDomainModel
import com.sagrishin.ptsadmapp.domain.services.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.joda.time.DateTime

class AppointmentsServiceImpl(
    private val db: Database,
    private val patientsService: PatientsService
) :  AppointmentsService {

    override fun getAll(): List<DomainAppointment> {
        return db.transaction {
            Appointment.all().map(Appointment::toDomainModel)
        }.sortedByDescending { it.dateTime }
    }

    override fun getBy(id: Long): DomainAppointment? {
        return db.transaction {
            Appointment.findById(id)?.let(Appointment::toDomainModel)
        }
    }

    override fun findBy(patient: DomainPatient): List<DomainAppointment> {
        return db.transaction {
            Appointment.find { Appointments.patientId eq patient.id }
                .map(Appointment::toDomainModel)
                .sortedByDescending { it.dateTime }
        }
    }

    override fun findBy(date: DateTime): List<DomainAppointment> {
        return db.transaction {
            Appointment.find { Appointments.dateTime eq date }
                .map(Appointment::toDomainModel)
                .sortedByDescending { it.dateTime }
        }
    }

    override fun findBetween(doctorId: Long, startDate: DateTime, endDate: DateTime): List<DomainDay> {
        return db.transaction {
            val patients = patientsService.getPatientsBy(doctorId)
            val patientsIds = patients.map { it.id }
            val appointments = Appointment.find {
                Appointments.dateTime.between(startDate, endDate) and (Appointments.patientId inList patientsIds)
            }.toList()

            (startDate..endDate).mapByDay { day ->
                getDomainDay(day, appointments.filter {
                    it.dateTime.withTimeAtStartOfDay() == day.withTimeAtStartOfDay()
                }, patients)
            }
        }
    }

    override fun findBetween(startDate: DateTime, endDate: DateTime): List<DomainAppointment> {
        return db.transaction {
            Appointment.find { Appointments.dateTime.between(startDate, endDate) }
                .map(Appointment::toDomainModel)
                .sortedByDescending { it.dateTime }
        }
    }

    override fun save(appointment: DomainAppointment, patientFor: DomainPatient): DomainAppointment {
        return db.transaction {
            (Appointment.findById(appointment.id)?.apply {
                patient = patientFor.id
                dateTime = appointment.dateTime
                description = appointment.description
            } ?: let { Appointment.new {
                patient = patientFor.id
                dateTime = appointment.dateTime
                description = appointment.description
            } }).let(Appointment::toDomainModel)
        }
    }

    override fun deleteBy(id: Long): Boolean {
        return db.transaction {
            Appointment.findById(id)?.delete()?.let { true } ?: false
        }
    }


    private fun getDomainDay(day: DateTime, appointments: List<Appointment>, patients: List<DomainPatient>): DomainDay {
        return DomainDay(
            day,
            appointments.map { appointment -> patients.find { it.id == appointment.toDomainModel().patientId }!! }
                        .map { patient -> getPatientWithAppointments(patient, appointments) }
        )
    }

    private fun getPatientWithAppointments(patient: DomainPatient, appointments: List<Appointment>): DomainPatient {
        return patient.copy(appointments = appointments
            .filter { it.patient == patient.id }
            .map(Appointment::toDomainModel))
    }

}
