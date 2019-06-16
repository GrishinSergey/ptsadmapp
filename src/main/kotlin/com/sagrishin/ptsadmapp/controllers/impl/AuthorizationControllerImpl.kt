package com.sagrishin.ptsadmapp.controllers.impl

import com.sagrishin.ptsadmapp.controllers.AuthorizationController
import com.sagrishin.ptsadmapp.domain.models.DomainUser
import com.sagrishin.ptsadmapp.domain.services.AuthorizationService
import io.ktor.auth.Principal
import org.apache.commons.codec.digest.DigestUtils.md5Hex

class AuthorizationControllerImpl(private val service: AuthorizationService) : AuthorizationController {

    override fun register(DomainUser: DomainUser): DomainUser? {
        return if (DomainUser.isUserDataFilled()) DomainUser.toDomainModel().let(service::register) else null
    }

    override fun authorize(DomainUser: DomainUser): DomainUser? {
        return if (DomainUser.isUserDataFilled()) DomainUser.toDomainModel().let(service::authorize) else null
    }

    override fun getUserBy(id: Long): DomainUser? {
        return if (id > 0) service.getUserBy(id) else null
    }

    override fun getUserPrincipalBy(id: Long): Principal? {
        return if (id > 0) service.getUserBy(id)?.toPrincipal() else null
    }


    private fun DomainUser.isUserDataFilled() = login.isNotBlank() && password.isNotBlank()

    private fun DomainUser.toDomainModel() = DomainUser(id, login, md5Hex(password).toUpperCase())

    private fun DomainUser.toPrincipal() = object : Principal { }

}
