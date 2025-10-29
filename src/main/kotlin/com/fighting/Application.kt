package com.fighting

import com.fighting.db.DatabaseFactory
import com.fighting.plugins.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    val httpClient = HttpClient(CIO){
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    DatabaseFactory.init()
    configureJWTAuthentication(httpClient)
    configureSerialization()
    configureRouting(httpClient)
}