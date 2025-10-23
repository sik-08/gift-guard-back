package com.fighting.plugins

import com.fighting.routes.gifticonRoutes
import com.fighting.routes.userRoutes
import com.fighting.services.GifticonService
import com.fighting.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // 서비스 인스턴스 생성
    val userService = UserService()
    val gifticonService = GifticonService()

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK,"Welcome to GiftGuard Backend!")
        }

        // 생성된 인스턴스를 라우트 함수에 전달
        userRoutes(userService)
        gifticonRoutes(gifticonService)
    }
}