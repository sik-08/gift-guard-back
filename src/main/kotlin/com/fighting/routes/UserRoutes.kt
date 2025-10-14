package com.fighting.routes

import com.fighting.models.DeviceTokenRequest
import com.fighting.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes() {
    authenticate {
        route("/api/v1/user") {
            post("/device") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asLong()
                val request = call.receive<DeviceTokenRequest>()

                UserService.registerOrUpdateDeviceToken(userId, request.fcmToken)
                call.respond(HttpStatusCode.OK, "Device token registered successfully.")
            }
        }
    }
}