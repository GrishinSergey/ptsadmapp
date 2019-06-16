package com.sagrishin.ptsadmapp.core.database

import org.hamcrest.core.Is.`is`
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.Assert.assertThat
import org.junit.Test

class DatabaseTest {

    @Test
    fun test() {
        val db = MySqlConfig().getDatabaseInstance()
        transaction (db) {
            val patient1 = Patient.new {
                this.name = "Lorem"
                this.surname = "Ipsum"
                this.phoneNumber = "+380971111111"
            }
            Appointment.new {
                this.dateTime = DateTime.now()
                this.patient = patient1.id.value
            }

            Patient[patient1.id.value].delete()
            val appointmentsCount = Appointment.find {
                Appointments.patientId eq patient1.id.value
            }.count()
            assertThat(appointmentsCount, `is`(0))
        }
    }

}
