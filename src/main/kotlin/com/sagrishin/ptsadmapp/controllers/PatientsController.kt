package com.sagrishin.ptsadmapp.controllers

import com.sagrishin.ptsadmapp.domain.models.DomainPatient

interface PatientsController {

    fun getAll(): List<DomainPatient>

    fun getAllBy(doctorId: Long): List<DomainPatient>

    fun getById(id: Long): DomainPatient?

    fun findByEntry(entry: String): List<DomainPatient>

    fun save(doctorId: Long, newPatient: DomainPatient): DomainPatient

    fun deletePatient(id: Long): Boolean

}
