package com.fighting.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.http.*
import java.util.*

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: ""
    private val issuer = System.getenv("JWT_ISSUER") ?: ""
    private val audience = System.getenv("JWT_AUDIENCE") ?: ""
    private val algorithm = Algorithm.HMAC256(secret)

    // 토큰 만료시간 (7일)
    private val validityInMs = 7 * 24 * 60 * 60 * 1000L

    fun makeToken(userId: Int, email: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + validityInMs))
            .sign(algorithm)
    }

    fun configureAuth(application: Application) {
        application.install(Authentication) {
            jwt("jwt-auth") {
                realm = issuer
                verifier(
                    JWT
                        .require(algorithm)
                        .withIssuer(issuer)
                        .withAudience(audience)
                        .build()
                )
                validate { credential ->
                    val subject = credential.payload.subject
                    if (subject != null) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token is invalid or expired"))
                }
            }
        }
    }
}