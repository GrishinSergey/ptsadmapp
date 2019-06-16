package com.sagrishin.ptsadmapp.api

import com.auth0.jwt.exceptions.JWTDecodeException
import com.sagrishin.ptsadmapp.controllers.AppointmentsController
import com.sagrishin.ptsadmapp.controllers.AuthorizationController
import com.sagrishin.ptsadmapp.controllers.PatientsController
import com.sagrishin.ptsadmapp.core.crypt.JwtUtil
import com.sagrishin.ptsadmapp.domain.models.DomainAppointment
import com.sagrishin.ptsadmapp.domain.models.DomainPatient
import com.sagrishin.ptsadmapp.domain.models.DomainUser
import de.nielsfalk.ktor.swagger.get
import de.nielsfalk.ktor.swagger.ok
import de.nielsfalk.ktor.swagger.responds
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.pipeline.PipelineContext
import org.joda.time.DateTime
import org.koin.ktor.ext.inject

fun Application.getRoutes(): Routing {
    val patientsController: PatientsController by inject()
    val appointmentsController: AppointmentsController by inject()
    val authorizationController: AuthorizationController by inject()

    return routing {

        route("authorization") {

            post("updateToken") {
                if (JwtUtil.isExpired(getPassedToken())) {
                    val doctorId = getRequestedUserId()
                    authorizationController.getUserBy(doctorId)?.let {
                        respond(JwtUtil.makeToken(it.id))
                    } ?: respondError("Can't create token. User with id $doctorId not found", "")
                }  else {
                    respond(getPassedToken())
                }
            }

            post("register") {
                val newUserData = call.receive<DomainUser>()

                authorizationController.register(newUserData)?.let {
                    respond(it)
                } ?: respondError("Can't create new user. Data is invalid")
            }

            post("authorize") {
                val user = call.receive<DomainUser>()
                authorizationController.authorize(user)?.let {
                    respond(JwtUtil.makeToken(it.id))
                } ?: respondError("Can't create token. User $user not found")
            }

        }

        authenticate("jwt") {

//            routing {
//                get<getAllByCurrentDoctor>(responds(ok<PatientsByDoctor>())) {
//                    val doctorId = getRequestedUserId()
//                    if (doctorId > 0) {
//                        respond(patientsController.getAllBy(doctorId))
//                    } else {
//                        respondError("Can't find user with id $doctorId")
//                    }
//                }
//            }

            route("patients") {

                get("getAllByCurrentDoctor") {
                    val doctorId = getRequestedUserId()
                    if (doctorId > 0) {
                        respond(patientsController.getAllBy(doctorId))
                    } else {
                        respondError("Can't find user with id $doctorId")
                    }
                }

                get("getAll") {
                    respond(patientsController.getAll())
                }

                get("getById/{id}") {
                    val id = call.parameters["id"]!!.toLong()
                    patientsController.getById(id)?.let {
                        respond(it)
                    } ?: respondError("Patient with id: $id not found")
                }

                get("getPatients/{start}/{count}") {
                    val start = call.parameters["start"]!!.toLong()
                    val count = call.parameters["count"]!!.toInt()

                    call.response.headers.append(REASON_MESSAGE, "Method not implemented")
                    call.response.status(HttpStatusCode.NotImplemented)
                    respond(Any())
                }

                get("findByEntry/{entryString}") {
                    val entryString = call.parameters["entryString"]!!
                    respond(patientsController.findByEntry(entryString))
                }

                post("save") {
                    val patient = call.receive<DomainPatient>()
                    val userId = getRequestedUserId()
                    respond(patientsController.save(userId, patient))
                }

                delete("deletePatientBy/{id}") {
                    val id = call.parameters["id"]!!.toLong()
                    respond(patientsController.deletePatient(id))
                }

            }

            route("appointments") {

                get("getAll") {
                    respond(appointmentsController.getAll())
                }

                get("getAppointmentsIn/{startDate}/{endDate}") {
                    val startDate = DateTime.parse(call.parameters["startDate"])
                    val endDate = DateTime.parse(call.parameters["endDate"])
                    val doctorId = getRequestedUserId()
                    respond(appointmentsController.getAppointmentsPerDaysBetween(doctorId, startDate, endDate))
                }

                get("findByPatient/{patientId}") {
                    val patientId = call.parameters["patientId"]!!.toLong()
                    respond(appointmentsController.findByPatient(patientId))
                }

                get("findByDate/{date}") {
                    val date = DateTime.parse(call.parameters["date"])
                    respond(appointmentsController.findByDate(date))
                }

                get("findByEntry/{entryString}") {
                    val entryString = call.parameters["entryString"]!!
                    if (entryString.isEmpty()) {
                        respondError("Entry string is empty", listOf<Any>())
                    } else {
                        val doctorId = getRequestedUserId()
                        if (doctorId > 0) {
                            try {
                                respond(appointmentsController.findByEntry(doctorId, entryString))
                            } catch (e: Exception) {
                                respondError(e.message!!, listOf<Any>())
                            }
                        } else {
                            respondError("Can't find user with id $doctorId", listOf<Any>())
                        }
                    }
                }

                post("addNewAppointment/{patientId}") {
                    val appointment = call.receive<DomainAppointment>()
                    val patientId = call.parameters["patientId"]!!.toLong()

                    appointmentsController.addNewAppointment(appointment, patientId)?.let {
                        respond(it)
                    } ?: respondError("An error acquired during adding appointment $appointment to patient: $patientId")
                }

                put("updateAppointment/{patientId}") {
                    val appointment = call.receive<DomainAppointment>()
                    val patientId = call.parameters["patientId"]!!.toLong()

                    try {
                        respond(appointmentsController.updateAppointment(appointment, patientId))
                    } catch (e: Exception) {
                        respondError(e.message!!)
                    }
                }

                delete("deleteAppointmentBy/{id}") {
                    val id = call.parameters["id"]!!.toLong()
                    respond(appointmentsController.deleteAppointment(id))
                }

            }

            route("dashboard") {

                get("getCalendar/{startDate}/{endDate}") {

                }

            }

        }

    }
}

private suspend inline fun PipelineContext<*, ApplicationCall>.respond(message: Any) {
    println(message)
    call.respond(message)
}


private suspend inline fun PipelineContext<Unit, ApplicationCall>.respondError(message: String, result: Any = Any()) {
    call.response.headers.append(REASON_MESSAGE, message)
    call.response.status(HttpStatusCode.BadRequest)
    respond(result)
}

private fun PipelineContext<Unit, ApplicationCall>.getRequestedUserId(): Long {
    return try {
        JwtUtil.parseJwt(getPassedToken())
            .claims[JwtUtil.SECRET_CLAIM_KEY]!!
            .asLong()
    } catch (e: JWTDecodeException) {
        -1L
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getPassedToken(): String {
    return (call.request.headers["Authorization"] ?: "").split(" ").last()
}
