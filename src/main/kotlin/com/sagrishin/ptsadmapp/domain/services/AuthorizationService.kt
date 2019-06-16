package com.sagrishin.ptsadmapp.domain.services;

import com.sagrishin.ptsadmapp.domain.models.DomainUser

interface AuthorizationService {

    fun register(userData: DomainUser): DomainUser?

    fun authorize(userData: DomainUser): DomainUser?

    fun getUserBy(id: Long): DomainUser?

}
