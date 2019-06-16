package com.sagrishin.ptsadmapp.domain.services.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sagrishin.*
import com.sagrishin.ptsadmapp.core.database.MySqlConfig
import com.sagrishin.ptsadmapp.core.database.Patient
import com.sagrishin.ptsadmapp.core.database.Patients
import org.hamcrest.core.Is.`is`
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PatientsServiceImplTest {

    private lateinit var service: PatientsServiceImpl
    private val db = MySqlConfig().getDatabaseInstance()

    private val expectedPatients = Gson().fromJson<List<TestPatient>>(
            File("src/test/resources/patients.json").readText(),
            object : TypeToken<List<TestPatient>>() {}.type
    ).map(::toDomainModel)

    @Before
    fun setUp() {
        service = PatientsServiceImpl(db)
        expectedPatients.map { patient -> Patient.new(db) {
            this.name = patient.name
            this.surname = patient.surname
            this.phoneNumber = patient.phoneNumber
        } }
    }

    @After
    fun tearDown() {
        transaction { Patients.deleteAll() }
    }

    @Test
    fun getAll() {
        val patients = service.getAll()
        assertThat(patients.size, `is`(expectedPatients.size))
        zip(patients, expectedPatients).forEach { (actual, expected) ->
            assertThat(actual.name, `is`(expected.name))
            assertThat(actual.surname, `is`(expected.surname))
            assertThat(actual.phoneNumber, `is`(expected.phoneNumber))
        }
    }

    @Test
    fun getBy() {
        val patients = transaction (db) { Patient.all().map(::toDomainModel) }
        val randomPatient = patients.random()
        val foundPatient = service.getBy(randomPatient.id)
        assertNotNull(foundPatient)
        foundPatient.let { (_, name, surname) ->
            assertThat(name, `is`(randomPatient.name))
            assertThat(surname, `is`(randomPatient.surname))
        }

        val notExistingPatient = service.getBy(-190)
        assertNull(notExistingPatient)
    }

    @Test
    fun findBy() {
        val entry = "LE"
        val foundPatients = service.findBy(entry)
        assertThat(foundPatients.size, `is`(2))
        foundPatients.forEach { assertContains("${it.name} ${it.surname}", entry, true) }
    }

    @Test
    fun saveOrUpdate() {
//        val patient = transaction {
//            Patient.find { (Patients.name eq "Nicolais") and (Patients.surname eq "Frangello") }
//                .first()
//                .let(::toDomainModel)
//        }
//        val updatedPatientValue = patient.copy(name = "Nicola", surname = "Frangell")
//        val updatedPatient = service.save(1, updatedPatientValue)
//        assertThat(updatedPatient.id, `is`(updatedPatientValue.id))
//        assertThat(updatedPatient.name, `is`(updatedPatientValue.name))
//        assertThat(updatedPatient.surname, `is`(updatedPatientValue.surname))
//        assertThat(updatedPatient.phoneNumber, `is`(updatedPatientValue.phoneNumber))
//
//        val newPatientValue = DomainPatient(-1, "NEW", "PATIENT", "1234567890", listOf())
//        val newPatient = service.save(newPatientValue)
//        assertThat(newPatient.name, `is`(newPatientValue.name))
//        assertThat(newPatient.surname, `is`(newPatientValue.surname))
//        assertThat(newPatient.phoneNumber, `is`(newPatientValue.phoneNumber))
    }

    @Test
    fun deleteBy() {
        val patient = transaction { Patient.find {
            (Patients.name eq "Kirbie") and (Patients.surname eq "Bangs")
        }.first().let { toDomainModel(it) } }
        val operationResult = service.deleteBy(patient.id)
        assertTrue(operationResult)
        assertNull(transaction { Patient.find {
            (Patients.name eq "Kirbie") and (Patients.surname eq "Bangs")
        }.firstOrNull() })

        val failedDeleteOperation = service.deleteBy(-190)
        assertFalse(failedDeleteOperation)
    }

}