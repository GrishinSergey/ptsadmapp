package com.sagrishin.ptsadmapp.domain.services.impl

import com.sagrishin.ptsadmapp.core.database.Appointment
import com.sagrishin.ptsadmapp.core.database.Appointments
import com.sagrishin.ptsadmapp.core.database.Patient
import com.sagrishin.ptsadmapp.core.database.Patients
import com.sagrishin.ptsadmapp.core.datetime.contains
import com.sagrishin.ptsadmapp.core.datetime.mapByDay
import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainDay
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import com.sagrishin.ptsadmapp.domain.services.PatientsService
import com.sagrishin.ptsadmapp.domain.services.toDomainModel
import com.sagrishin.ptsadmapp.domain.services.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.joda.time.DateTime

class PatientsServiceImpl(private val db: Database) : PatientsService {

    companion object {
        const val MAX_APPOINTMENTS_PER_PATIENT = 5
    }

    override fun getAll(): List<DomainPatient> {
        return db.transaction {
            Patient.all().map { it.toDomainModelWithAppointments() }
        }
    }

    override fun getAllBy(doctorId: Long): List<DomainPatient> {
        return db.transaction {
            Patient.find { (Patients.doctorId eq doctorId) }.map { it.toDomainModelWithAppointments() }
        }
    }

    override fun getPatientsBy(doctorId: Long): List<DomainPatient> {
        return db.transaction {
            Patient.find { (Patients.doctorId eq doctorId) }.map { it.toDomainModel() }
        }
    }

    override fun getBy(id: Long): DomainPatient? {
        return db.transaction {
            Patient.findById(id)?.toDomainModelWithAppointments()
        }
    }

    override fun findBy(entry: String): List<DomainPatient> {
        return db.transaction {
            Patient.find { (Patients.name like "%$entry%") or (Patients.surname like "%$entry%") }
                .map { it.toDomainModelWithAppointments() }
        }
    }

    override fun save(doctorId: Long, newPatient: DomainPatient): DomainPatient {
        return db.transaction {
            (Patient.findById(newPatient.id)?.apply {
                phoneNumber = newPatient.phoneNumber
                name = newPatient.name
                surname = newPatient.surname
            } ?: let { Patient.new {
                phoneNumber = newPatient.phoneNumber
                name = newPatient.name
                surname = newPatient.surname
                doctor = doctorId
            } }).toDomainModelWithAppointments()
        }
    }

    override fun deleteBy(id: Long): Boolean {
        return db.transaction {
            Patient.findById(id)?.delete()?.let { true } ?: false
        }
    }

    override fun findPatientsByEntryWithAllAppointments(doctorId: Long, entry: String): List<DomainPatient> {
        return db.transaction {
            Patient.find {
                (Patients.doctorId eq doctorId) and
                ((Patients.name like "%$entry%") or (Patients.surname like "%$entry%"))
            }.map { patient ->
                Appointment.find { Appointments.patientId eq patient.id.value }
                    .sortedByDescending { it.dateTime }
                    .map(Appointment::toDomainModel)
                    .let(patient::toDomainModel)
            }
        }
    }

    override fun splitPatientsByDays(patients: List<DomainPatient>): List<DomainDay> {
        val appointments = patients.flatMap { it.appointments }
        if (appointments.isNotEmpty()) {
            val minDate = appointments.minBy { it.dateTime }?.dateTime!!
            val maxDate = appointments.maxBy { it.dateTime }?.dateTime!!
            return (minDate..maxDate).mapByDay { day -> getDay(day, appointments, patients) }
        }
        return emptyList()
    }


    private fun getDay(day: DateTime, appointments: List<DomainAppointment>, patients: List<DomainPatient>): DomainDay {
        return DomainDay(
            day,
            appointments.filter { it.dateTime in day }.map { appointment ->
                patients.find { it.id == appointment.patientId }!!.copy(appointments = listOf(appointment))
            }
        )
    }

    private fun Patient.toDomainModelWithAppointments(): DomainPatient {
        return toDomainModel(getLastAppointments(id.value))
    }

    private fun getLastAppointments(patientId: Long): List<DomainAppointment> {
        return Appointment.find { Appointments.patientId eq patientId }
                .limit(MAX_APPOINTMENTS_PER_PATIENT)
                .sortedByDescending { it.dateTime }
                .map(Appointment::toDomainModel)
    }

}
