package com.sagrishin.ptsadmapp.controllers

import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainDay
import org.joda.time.DateTime

interface AppointmentsController {

    fun getAll(): List<DomainAppointment>

    fun getAppointmentsPerDaysBetween(doctorId: Long, start: DateTime, end: DateTime): List<DomainDay>

    fun getAppointmentsBetween(start: DateTime, end: DateTime): List<DomainAppointment>

    fun findByPatient(patientId: Long): List<DomainAppointment>

    fun findByDate(date: DateTime): List<DomainAppointment>

    fun findByEntry(doctorId: Long, entryString: String): List<DomainDay>

    fun addNewAppointment(data: DomainAppointment, patientId: Long): DomainAppointment?

    fun updateAppointment(data: DomainAppointment, patientId: Long): DomainAppointment

    fun deleteAppointment(id: Long): Boolean

}
