package com.fighting.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respondText
import java.util.Date

private val secret = System.getenv("JWT_SECRET")
private val issuer = System.getenv("JWT_ISSUER")
private val audience = System.getenv("JWT_AUDIENCE")
private val algorithm = Algorithm.HMAC256(secret)
private const val validityInMs = 7 * 24 * 60 * 60 * 1000L

fun Application.configureJWTAuthentication(httpClient: HttpClient) {
    install(Authentication) {
        jwt("jwt-auth"){
            realm = issuer

            val jwtVerifier = JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build()

            verifier(jwtVerifier)

            validate { jwtCredential ->
                val subject = jwtCredential.payload.subject
                if (subject != null) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respondText("Token is nor valid or has expired",
                    status = HttpStatusCode.Unauthorized)
            }


        }
        configureGoogleOAuth(httpClient)
    }
}

fun makeToken(userId: String, email: String): String {
    val now = System.currentTimeMillis()
    return JWT.create()
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(userId)
        .withClaim("email", email)
        .withIssuedAt(Date(now))
        .withExpiresAt(Date(now + validityInMs))
        .sign(algorithm)
}