package com.sagrishin.ptsadmapp.domain.services

import com.sagrishin.ptsadmapp.domain.models.DomainDay
import com.sagrishin.ptsadmapp.domain.models.DomainPatient

interface PatientsService {

    fun getAllBy(doctorId: Long): List<DomainPatient>

    fun getPatientsBy(doctorId: Long): List<DomainPatient>

    fun getAll(): List<DomainPatient>

    fun getBy(id: Long): DomainPatient?

    fun findBy(entry: String): List<DomainPatient>

    fun findPatientsByEntryWithAllAppointments(doctorId: Long, entry: String): List<DomainPatient>

    fun splitPatientsByDays(patients: List<DomainPatient>): List<DomainDay>

    fun save(doctorId: Long, newPatient: DomainPatient): DomainPatient

    fun deleteBy(id: Long): Boolean

}
