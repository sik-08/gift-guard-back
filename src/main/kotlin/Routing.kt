/*
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
class Articles(val sort: String? = "new")*/
// Routing.kt

package com.fighting

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*

fun Application.configureRouting(userService: UserService) {
    // 라우팅 설정 시작 (플러그인 설치 코드는 모두 제거됨)
    routing {

        get("/") {
            call.respondText("Hello World!")
        }

        staticResources("/static", "static")

        // ⭐ 사용자(User) 관련 CRUD 라우팅
        route("/users") {

            // 👇👇👇 모든 사용자 조회 API 추가 👇👇👇
            // GET /users
            get {
                val users = userService.readAll()
                call.respond(HttpStatusCode.OK, users)
            }

            // POST /users
            post {
                val user = call.receive<ExposedUser>()
                val id = userService.create(user)
                call.respond(HttpStatusCode.Created, id)
            }

            // /users/{id} 경로 그룹
            route("/{id}") {

                // GET /users/{id}
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

                // PUT /users/{id}
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

                // DELETE /users/{id}
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