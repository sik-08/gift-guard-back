package com.fighting.routes

import com.fighting.models.AuthResponse
import com.fighting.models.GoogleLoginRequest
import com.fighting.models.LocalLoginRequest
import com.fighting.plugins.generateJwtToken
import com.fighting.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    route("/api/v1/auth") {
        post("/google") {
            val request = call.receive<GoogleLoginRequest>()
            val user = AuthService.verifyAndProcessUser(request.idToken)

            if (user != null) {
                val accessToken = generateJwtToken(user)
                call.respond(HttpStatusCode.OK, AuthResponse(accessToken))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid Google Token")
            }
        }

        // 로컬 테스트용
        post("/local/login") {
            val request = call.receive<LocalLoginRequest>()
            val user = AuthService.findOrCreateUserForLocalTest(request.email, request.displayName)

            val accessToken = generateJwtToken(user)
            call.respond(HttpStatusCode.OK, AuthResponse(accessToken))
        }
    }
}