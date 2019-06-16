package com.sagrishin.ptsadmapp.domain.models

import org.joda.time.DateTime

data class DomainPatient(
    val id: Long,
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val appointments: List<DomainAppointment> = emptyList()
)


data class DomainAppointment(
    val id: Long,
    val description: String,
    val dateTime: DateTime,
    val patientId: Long? = null
)


data class DomainUser(
    val id: Long,
    val login: String,
    val password: String
)


data class DomainDay(
    val date: DateTime,
    val patients: List<DomainPatient>
)
