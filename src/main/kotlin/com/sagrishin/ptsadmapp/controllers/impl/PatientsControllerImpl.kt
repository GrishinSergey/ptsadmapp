package com.sagrishin.ptsadmapp.controllers.impl

import com.sagrishin.ptsadmapp.controllers.PatientsController
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import com.sagrishin.ptsadmapp.domain.services.PatientsService

class PatientsControllerImpl(
    private val patientService: PatientsService
) : PatientsController {

    override fun getAll(): List<DomainPatient> {
        return patientService.getAll()
    }

    override fun getAllBy(doctorId: Long): List<DomainPatient> {
        return patientService.getAllBy(doctorId)
    }

    override fun getById(id: Long): DomainPatient? {
        return patientService.getBy(id)
    }

    override fun findByEntry(entry: String): List<DomainPatient> {
        return if (entry.isNotEmpty()) patientService.findBy(entry) else emptyList()
    }

    override fun save(doctorId: Long, newPatient: DomainPatient): DomainPatient {
        return patientService.save(doctorId, newPatient)
    }

    override fun deletePatient(id: Long): Boolean {
        return patientService.deleteBy(id)
    }

}
