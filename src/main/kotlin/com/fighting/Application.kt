package com.fighting

import com.fighting.db.DatabaseFactory
import com.fighting.plugins.*
import com.fighting.services.FCMService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    // FCMService.init() // 로컬 테스트 - FCM 비활성화
    configureSecurity()
    configureSerialization()
    configureRouting()
}