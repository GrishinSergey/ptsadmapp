package com.sagrishin.ptsadmapp.core.crypt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.Payload
import org.joda.time.DateTime
import java.util.*

object JwtUtil {

    const val SECRET_CLAIM_KEY = "uid"

    private const val EXP_CLAIM_KEY = "exp"
    private const val SUB_CLAIM_VALUE = "Authentication"
    private const val ISSUER = "ktor.io"
    private const val VALIDITY_IN_MS = 36_000_000 // 10 hours

    private val algorithm = Algorithm.HMAC512(AesCryptUseCase.SECRET_KEY)

    val verifier: JWTVerifier by lazy { JWT.require(algorithm).withIssuer(ISSUER).build() }

    fun makeToken(userId: Long): String {
        return JWT.create()
            .withSubject(SUB_CLAIM_VALUE)
            .withIssuer(ISSUER)
            .withClaim(SECRET_CLAIM_KEY, userId)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
            .sign(algorithm)
    }

    fun parseJwt(token: String): Payload {
        return JWT.decode(token)
    }

    fun isExpired(token: String): Boolean {
        return try {
            DateTime(parseJwt(token).claims[EXP_CLAIM_KEY]?.asDate()).isBeforeNow
        } catch (e: JWTDecodeException) {
            true
        }
    }

}
