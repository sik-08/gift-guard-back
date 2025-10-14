package com.fighting.plugins

import com.fighting.routes.authRoutes
import com.fighting.routes.gifticonRoutes
import com.fighting.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to GiftGuard Backend!")
        }
        authRoutes()
        gifticonRoutes()
        userRoutes()
    }
}