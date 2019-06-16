package com.sagrishin.ptsadmapp.domain.services.impl

import com.sagrishin.ptsadmapp.core.database.User
import com.sagrishin.ptsadmapp.core.database.Users
import com.sagrishin.ptsadmapp.domain.models.DomainUser
import com.sagrishin.ptsadmapp.domain.services.AuthorizationService
import com.sagrishin.ptsadmapp.domain.services.toDomainModel
import com.sagrishin.ptsadmapp.domain.services.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and

class AuthorizationServiceImpl(private val db: Database) : AuthorizationService {

    override fun register(userData: DomainUser): DomainUser? {
        return db.transaction {
            User.new {
                this.login = userData.login
                this.password = userData.password
            }
        }.toDomainModel()
    }

    override fun authorize(userData: DomainUser): DomainUser? {
        return db.transaction {
            User.find {
                (Users.login eq userData.login) and (Users.password eq userData.password)
            }.singleOrNull()?.toDomainModel()
        }
    }

    override fun getUserBy(id: Long): DomainUser? {
        return db.transaction {
            User.findById(id)?.toDomainModel()
        }
    }

}
