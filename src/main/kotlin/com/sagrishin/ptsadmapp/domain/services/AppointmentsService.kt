package com.sagrishin.ptsadmapp.domain.services

import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainDay
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import org.joda.time.DateTime

interface AppointmentsService {

    fun getAll(): List<DomainAppointment>

    fun getBy(id: Long): DomainAppointment?

    fun findBy(patient: DomainPatient): List<DomainAppointment>

    fun findBy(date: DateTime): List<DomainAppointment>

    fun findBetween(doctorId: Long, startDate: DateTime, endDate: DateTime): List<DomainDay>

    fun findBetween(startDate: DateTime, endDate: DateTime): List<DomainAppointment>

    fun save(appointment: DomainAppointment, patientFor: DomainPatient): DomainAppointment

    fun deleteBy(id: Long): Boolean

}
