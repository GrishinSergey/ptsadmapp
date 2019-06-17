package com.sagrishin.ptsadmapp.core.database

import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Cast
import org.jetbrains.exposed.sql.DateColumnType
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.castTo
import org.joda.time.DateTime

object Patients : LongIdTable() {
    val name = varchar("name", 100).uniqueIndex("patientName")
    val surname = varchar("surname", 100).uniqueIndex("patientSurname")
    val phoneNumber = varchar("phoneNumber", 100).uniqueIndex()
    val doctorId = long("doctorId").references(Users.long("id"), CASCADE, CASCADE)
}


object Appointments : LongIdTable() {
    val dateTime = datetime("dateTime")
    val description = text("description")
    val patientId = long("patientId").references(Patients.long("id"), CASCADE, CASCADE)

    val date: Cast<DateTime> get() = dateTime.castTo(DateColumnType(false))
}


object Users: LongIdTable() {
    val login = varchar("login", 100).uniqueIndex("user")
    val password = varchar("password", 100).uniqueIndex("user")
}
