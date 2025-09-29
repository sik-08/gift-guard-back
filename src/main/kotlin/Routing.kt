package com.fighting

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun Application.configureRouting(userService: UserService) {
    routing {

        get("/") {
            call.respondText("Hello World!")
        }

        staticResources("/static", "static")

        route("/users") {
            get {
                val users = userService.readAll()
                call.respond(HttpStatusCode.OK, users)
            }

            post {
                val user = call.receive<ExposedUser>()
                val id = userService.create(user)
                call.respond(HttpStatusCode.Created, id)
            }

            route("/{id}") {
                get {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID parameter must be an integer.")
                        return@get
                    }

                    val user = userService.read(id)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User with ID $id not found.")
                    }
                }

                put {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID parameter must be an integer.")
                        return@put
                    }

                    val user = call.receive<ExposedUser>()
                    val updatedRows = userService.update(id, user)
                    if (updatedRows > 0) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User with ID $id not found.")
                    }
                }

                delete {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "ID parameter must be an integer.")
                        return@delete
                    }

                    val deletedRows = userService.delete(id)
                    if (deletedRows > 0) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User with ID $id not found.")
                    }
                }
            }
        }
    }
}