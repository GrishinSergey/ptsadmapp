package com.sagrishin.ptsadmapp.controllers.impl

import com.sagrishin.ptsadmapp.controllers.AppointmentsController
import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainDay
import com.sagrishin.ptsadmapp.domain.services.AppointmentsService
import com.sagrishin.ptsadmapp.domain.services.PatientsService
import org.joda.time.DateTime

class AppointmentsControllerImpl(
    private val appointmentsService: AppointmentsService,
    private val patientsService: PatientsService
) : AppointmentsController {

    override fun getAll(): List<DomainAppointment> {
        return appointmentsService.getAll()
    }

    override fun getAppointmentsPerDaysBetween(doctorId: Long, start: DateTime, end: DateTime): List<DomainDay> {
        return if (start > end) {
            emptyList()
        } else {
            appointmentsService.findBetween(doctorId, start.withTimeAtStartOfDay(), end.withTimeAtStartOfDay())
        }
    }

    override fun getAppointmentsBetween(start: DateTime, end: DateTime): List<DomainAppointment> {
        return if (start > end) {
            emptyList()
        } else {
            appointmentsService.findBetween(start.withTimeAtStartOfDay(), end.withTimeAtStartOfDay())
        }
    }

    override fun findByPatient(patientId: Long): List<DomainAppointment> {
        return patientsService.getBy(patientId)?.let(appointmentsService::findBy) ?: listOf()
    }

    override fun findByDate(date: DateTime): List<DomainAppointment> {
        return appointmentsService.findBy(date.withTimeAtStartOfDay())
    }

    override fun findByEntry(doctorId: Long, entryString: String): List<DomainDay> {
        return patientsService.findPatientsByEntryWithAllAppointments(doctorId, entryString)
            .let(patientsService::splitPatientsByDays)
    }

    override fun addNewAppointment(data: DomainAppointment, patientId: Long): DomainAppointment? {
        return patientsService.getBy(patientId)?.let { appointmentsService.save(data, it) }
    }

    override fun updateAppointment(data: DomainAppointment, patientId: Long): DomainAppointment {
        return patientsService.getBy(patientId)?.let { patientFor ->
            appointmentsService.save(data, patientFor)
        } ?: let {
            throw RuntimeException("Unexpected error: patient with id($patientId) not found")
        }
    }

    override fun deleteAppointment(id: Long): Boolean {
        return appointmentsService.deleteBy(id)
    }
    
}
