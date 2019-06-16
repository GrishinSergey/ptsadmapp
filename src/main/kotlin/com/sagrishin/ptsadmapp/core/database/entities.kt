package com.sagrishin.ptsadmapp.core.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

class Patient(id: EntityID<Long>): LongEntity(id) {

    companion object : LongEntityClass<Patient>(Patients)

    var name by Patients.name
    var surname by Patients.surname
    var phoneNumber by Patients.phoneNumber
    var doctor by Patients.doctorId

}

class Appointment(id: EntityID<Long>): LongEntity(id) {

    companion object : LongEntityClass<Appointment>(Appointments)

    var dateTime by Appointments.dateTime
    var description by Appointments.description
    var patient by Appointments.patientId

}

class User(id: EntityID<Long>): LongEntity(id) {

    companion object : LongEntityClass<User>(Users)

    var login by Users.login
    var password by Users.password

}
