package com.fighting

import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.application.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import kotlinx.serialization.Serializable
import org.slf4j.event.*

fun Application.configureRouting(userService: UserService) {
    install(DoubleReceive)
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        // -----------------------------------------------------------------
        // ⭐ 기존 라우팅 (Hello World, Double Receive, Articles)

        get("/") {
            call.respondText("Hello World!")
        }
        post("/double-receive") {
            val first = call.receiveText()
            val theSame = call.receiveText()
            call.respondText(first + " " + theSame)
        }
        get<Articles> { article ->
            call.respond("List of articles sorted starting from ${article.sort}")
        }

        staticResources("/static", "static")

        // -----------------------------------------------------------------
        // ⭐ 사용자(User) 관련 CRUD 라우팅 (UserService 사용)

        // Create user
        post("/users") {
            val user = call.receive<ExposedUser>()
            val id = userService.create(user)
            call.respond(HttpStatusCode.Created, id)
        }

        // Read user
        get("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = userService.read(id)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Update user
        put("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val user = call.receive<ExposedUser>()
            userService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }

        // Delete user
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            userService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
        // -----------------------------------------------------------------
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")