package com.sagrishin.ptsadmapp.di

import com.google.gson.GsonBuilder
import com.sagrishin.ptsadmapp.api.DateTimeTypeAdapter
import com.sagrishin.ptsadmapp.controllers.AppointmentsController
import com.sagrishin.ptsadmapp.controllers.AuthorizationController
import com.sagrishin.ptsadmapp.controllers.PatientsController
import com.sagrishin.ptsadmapp.controllers.impl.AppointmentsControllerImpl
import com.sagrishin.ptsadmapp.controllers.impl.AuthorizationControllerImpl
import com.sagrishin.ptsadmapp.controllers.impl.PatientsControllerImpl
import com.sagrishin.ptsadmapp.core.database.MySqlConfig
import com.sagrishin.ptsadmapp.core.database.PostgreSqlConfig
import com.sagrishin.ptsadmapp.domain.services.AppointmentsService
import com.sagrishin.ptsadmapp.domain.services.AuthorizationService
import com.sagrishin.ptsadmapp.domain.services.PatientsService
import com.sagrishin.ptsadmapp.domain.services.impl.AppointmentsServiceImpl
import com.sagrishin.ptsadmapp.domain.services.impl.AuthorizationServiceImpl
import com.sagrishin.ptsadmapp.domain.services.impl.PatientsServiceImpl
import org.joda.time.DateTime
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

val appModule = module(createdAtStart = true) {

    single(StringQualifier("release")) { PostgreSqlConfig().getDatabaseInstance() }

//    single(StringQualifier("debug")) { MySqlConfig().getDatabaseInstance() }

    single {
        GsonBuilder()
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter.getDeserializer())
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter.getSerializer())
            .create()
    }

}

val patientsModule = module(createdAtStart = true) {

    singleBy<PatientsController, PatientsControllerImpl>()

    singleBy<PatientsService, PatientsServiceImpl>()

}

val appointmentsModule = module(createdAtStart = true) {

    singleBy<AppointmentsController, AppointmentsControllerImpl>()

    singleBy<AppointmentsService, AppointmentsServiceImpl>()

}

val authorizationModule = module(createdAtStart = true) {

    singleBy<AuthorizationController, AuthorizationControllerImpl>()

    singleBy<AuthorizationService, AuthorizationServiceImpl>()

}
