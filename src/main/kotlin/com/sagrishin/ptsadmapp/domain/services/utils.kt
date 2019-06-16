package com.sagrishin.ptsadmapp.domain.services

import com.sagrishin.ptsadmapp.core.database.Appointment
import com.sagrishin.ptsadmapp.core.database.Patient
import com.sagrishin.ptsadmapp.core.database.User
import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import com.sagrishin.ptsadmapp.domain.models.DomainUser
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

fun <T> Database.transaction(statement: Transaction.() -> T): T = transaction(this, statement)

fun Appointment.toDomainModel(): DomainAppointment = DomainAppointment(id.value, description, dateTime, patient)

fun User.toDomainModel(): DomainUser = DomainUser(id.value, login, password)

fun Patient.toDomainModel(appointments: List<DomainAppointment> = emptyList()): DomainPatient = DomainPatient(
    id = id.value,
    name = name,
    surname = surname,
    phoneNumber = phoneNumber,
    appointments = appointments
)
