package com.fighting.routes

import com.fighting.models.GifticonCreateRequest
import com.fighting.models.GifticonUpdateRequest
import com.fighting.services.GifticonService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gifticonRoutes() {
    authenticate {
        route("/api/v1/gifticons") {
            get {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asLong()
                val gifticons = GifticonService.findByUser(userId)
                call.respond(HttpStatusCode.OK, gifticons)
            }
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("userId").asLong()
                val request = call.receive<GifticonCreateRequest>()
                val newGifticon = GifticonService.create(userId, request)
                call.respond(HttpStatusCode.Created, newGifticon)
            }
            route("/{id}") {
                put {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("userId").asLong()
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    val request = call.receive<GifticonUpdateRequest>()

                    if (GifticonService.update(id, userId, request)) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
                delete {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("userId").asLong()
                    val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)

                    if (GifticonService.delete(id, userId)) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
}