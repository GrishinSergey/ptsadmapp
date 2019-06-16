package com.sagrishin.ptsadmapp.controllers;

import com.sagrishin.ptsadmapp.domain.models.DomainUser
import io.ktor.auth.Principal

interface AuthorizationController {

    fun register(DomainUser: DomainUser): DomainUser?

    fun authorize(DomainUser: DomainUser): DomainUser?

    fun getUserBy(id: Long): DomainUser?

    fun getUserPrincipalBy(id: Long): Principal?

}
