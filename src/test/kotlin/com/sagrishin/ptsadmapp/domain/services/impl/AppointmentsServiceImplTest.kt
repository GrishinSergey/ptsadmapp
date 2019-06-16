package com.sagrishin.ptsadmapp.domain.services.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sagrishin.*
import com.sagrishin.ptsadmapp.core.database.Appointment
import com.sagrishin.ptsadmapp.core.database.MySqlConfig
import com.sagrishin.ptsadmapp.core.database.Patient
import com.sagrishin.ptsadmapp.core.database.Patients
import org.hamcrest.core.Is
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File

class AppointmentsServiceImplTest {

    private lateinit var patientsService: PatientsServiceImpl
    private lateinit var appointmentService: AppointmentsServiceImpl
    private val db = MySqlConfig().getDatabaseInstance()

    private val expectedPatients = Gson().fromJson<List<TestPatient>>(
        File("src/test/resources/patients.json").readText(),
        object : TypeToken<List<TestPatient>>() {}.type
    ).map(::toDomainModel)
    private val expectedAppointments = Gson().fromJson<List<TestAppointment>>(
        File("src/test/resources/appointments.json").readText(),
        object : TypeToken<List<TestAppointment>>() { }.type
    ).map { appointment ->
        val formatter = DateTimeFormat.forPattern("DD/MM/YYYY HH:mm:ss.SSS")
        Appointment.new(db) {
            this.dateTime = DateTime.parse(appointment.dateTime, formatter)
            this.patient = expectedPatients.random().id
            this.description = ""
        }
    }

    @Before
    fun setUp() {
        patientsService = PatientsServiceImpl(db)
        appointmentService = AppointmentsServiceImpl(db, patientsService)
        expectedPatients.map { patient -> Patient.new(db) {
            this.name = patient.name
            this.surname = patient.surname
            this.phoneNumber = patient.phoneNumber
        } }
        expectedAppointments.map { appointment -> Appointment.new(db) {
            this.dateTime = appointment.dateTime
            this.patient = appointment.patient
        } }
    }

    @After
    fun tearDown() {
        transaction { Patients.deleteAll() }
    }

    @Test
    fun getAll() {
        val appointments = appointmentService.getAll()
        assertThat(appointments.size, Is.`is`(expectedPatients.size))
        zip(appointments, expectedAppointments).forEach { (actual, expected) ->
            assertThat(actual.dateTime, Is.`is`(expected.dateTime))
        }
    }

    @Test
    fun findBetween() {
        val start = DateTime.now().minusWeeks(1)
        val end = DateTime.now().plusWeeks(1)
        val doctorId = 1L
        val days = appointmentService.findBetween(doctorId, start, end)
        days
    }

}