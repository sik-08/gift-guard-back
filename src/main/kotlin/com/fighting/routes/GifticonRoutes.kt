package com.fighting.routes

import com.fighting.auth.getUserId
import com.fighting.models.Gifticon
import com.fighting.models.GifticonRequest
import com.fighting.services.GifticonService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gifticonRoutes(gifticonService: GifticonService) {
    authenticate("jwt-auth") {
        route("/api/v1/gifticons") {
            // GET /: 내 기프티콘 목록 조회
            get {
                val userId = call.getUserId()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                val gifticons = gifticonService.getGifticonsByUser(userId)
                call.respond(HttpStatusCode.OK, gifticons)
            }

            // POST /: 새 기프티콘 등록
            post {
                val userId = call.getUserId()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }

                // GifticonRequest DTO 사용
                val request = call.receive<GifticonRequest>()

                // DTO와 JWT의 userId를 조합해 Gifticon 객체 생성
                val newGifticon = Gifticon(
                    userId = userId,
                    storeName = request.storeName,
                    expirationDate = request.expirationDate,
                    isUsed = false
                )

                val created = gifticonService.createGifticon(newGifticon)
                call.respond(HttpStatusCode.Created, created)
            }

            route("/{id}") {
                // GET /{id}: 내 기프티콘 상세 조회
                get {
                    val userId = call.getUserId()
                    val gifticonId = call.parameters["id"]?.toIntOrNull()
                    if (userId == null || gifticonId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }

                    val gifticon = gifticonService.getGifticonById(gifticonId)

                    // 토큰의 userId와 기프티콘의 소유자(userId)가 일치하는지 확인
                    if (gifticon == null || gifticon.userId != userId) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Gifticon not found or access denied"))
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, gifticon)
                }

                // PUT /{id}: 내 기프티콘 수정 (사용 여부 변경 등)
                put {
                    val userId = call.getUserId()
                    val gifticonId = call.parameters["id"]?.toIntOrNull()
                    if (userId == null || gifticonId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@put
                    }

                    val request = call.receive<GifticonRequest>()

                    // 수정 전 소유권 확인
                    val existing = gifticonService.getGifticonById(gifticonId)
                    if (existing == null || existing.userId != userId) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Gifticon not found or access denied"))
                        return@put
                    }

                    // 수정된 객체 생성 (isUsed는 기존 값 유지)
                    val updatedGifticon = existing.copy(
                        storeName = request.storeName,
                        expirationDate = request.expirationDate
                    )

                    val success = gifticonService.updateGifticon(gifticonId, updatedGifticon)
                    if (success) {
                        call.respond(HttpStatusCode.OK, updatedGifticon)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Update failed"))
                    }
                }

                // DELETE /{id}: 내 기프티콘 삭제
                delete {
                    val userId = call.getUserId()
                    val gifticonId = call.parameters["id"]?.toIntOrNull()
                    if (userId == null || gifticonId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@delete
                    }

                    // 삭제 전 소유권 확인
                    val existing = gifticonService.getGifticonById(gifticonId)
                    if (existing == null || existing.userId != userId) {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Gifticon not found or access denied"))
                        return@delete
                    }

                    val success = gifticonService.deleteGifticon(gifticonId)
                    if (success) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Delete failed"))
                    }
                }
            }
        }
    }
}