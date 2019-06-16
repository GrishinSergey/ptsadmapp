package com.sagrishin.ptsadmapp

import com.sagrishin.ptsadmapp.api.DateTimeTypeAdapter.getDeserializer
import com.sagrishin.ptsadmapp.api.DateTimeTypeAdapter.getSerializer
import com.sagrishin.ptsadmapp.api.REASON_MESSAGE
import com.sagrishin.ptsadmapp.api.getRoutes
import com.sagrishin.ptsadmapp.controllers.AuthorizationController
import com.sagrishin.ptsadmapp.core.crypt.JwtUtil
import com.sagrishin.ptsadmapp.di.appModule
import com.sagrishin.ptsadmapp.di.appointmentsModule
import com.sagrishin.ptsadmapp.di.authorizationModule
import com.sagrishin.ptsadmapp.di.patientsModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.joda.time.DateTime
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun main() {
    val port: Int = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(factory = Netty, port = port) {
        install(CallLogging)
        installKoin()
        installJsonConverter()
        installCompression()
        installHeaders()
        installAuth()

//        install(Locations)
//        install(SwaggerSupport) {
//            forwardRoot = true
//            val information = Information(
//                version = "v1.0-alpha"
//            )
//            swagger = Swagger().apply {
//                info = information
//                definitions["patientUuid"] = patientSchema
//            }
//            openApi = OpenApi().apply {
//                info = information
//                components.schemas["size"] = sizeSchemaMap
//                components.schemas[petUuid] = petIdSchema
//                components.schemas["Rectangle"] = rectangleSchemaMap("#/components/schemas")
//            }
//        }

        installRoutes()
    }.start(true)
}

fun Application.installKoin() {
    install(Koin) {
        modules(appModule, patientsModule, appointmentsModule, authorizationModule)
    }
}

fun Application.installJsonConverter() {
    install(DataConversion) {
        convert<DateTime> {
            decode { values, _ -> values.singleOrNull()?.let { DateTime.parse(it) } }
        }
    }
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(DateTime::class.java, getSerializer())
            registerTypeAdapter(DateTime::class.java, getDeserializer())
            setPrettyPrinting()
        }
    }
}

fun Application.installCompression() {
//    install(Compression) {
//        gzip {
//            priority = 1.0
//        }
//        deflate {
//            priority = 10.0
//            minimumSize(1024)
//        }
//    }
}

fun Application.installHeaders() {
    install(DefaultHeaders)
    install(ConditionalHeaders)
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        exposedHeaders += REASON_MESSAGE
        anyHost()
    }
}

fun Application.installAuth() {
    val authorizationController: AuthorizationController by inject()

    install(Authentication) {
        jwt("jwt") {
            verifier(JwtUtil.verifier)
            realm = "ktor.io"
            validate { credentials ->
                val claim = credentials.payload.claims[JwtUtil.SECRET_CLAIM_KEY]
                claim?.asLong()?.let(authorizationController::getUserPrincipalBy)
            }
        }
    }
}

fun Application.installRoutes() {
    routing { getRoutes() }
}
