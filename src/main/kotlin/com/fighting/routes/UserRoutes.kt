package com.fighting.routes

import com.fighting.auth.GoogleAuthService
import com.fighting.auth.JwtConfig
import com.fighting.auth.getUserId
import com.fighting.models.AuthResponse
import com.fighting.models.GoogleLoginRequest
import com.fighting.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    route("/api/v1/auth") {
        /**
         * POST /api/v1/auth/google
         * 구글 로그인 및 자동 회원가입
         * - 클라이언트에서 받은 Google ID Token 검증
         * - DB에 사용자가 없으면 생성, 있으면 정보 업데이트
         * - 서버 자체 JWT 토큰 발급
         */

        get("/") {
            val users = userService.getAllUsers()
            call.respond(HttpStatusCode.OK, users)
        }

        post("/google") {
            try {
                val request = call.receive<GoogleLoginRequest>()
                val payload = GoogleAuthService.verifyIdToken(request.idToken)

                if (payload == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid Google ID Token"))
                    return@post
                }

                val user = userService.createOrUpdateFromGoogle(
                    googleId = payload.googleId,
                    email = payload.email,
                    name = payload.name
                )

                // user.userId가 null이 아님을 확신 (DB에서 생성/조회했으므로)
                val appToken = JwtConfig.makeToken(user.userId!!, user.email)

                call.respond(HttpStatusCode.OK, AuthResponse(token = appToken))

            } catch (e: Exception) {
                // SerializationException (잘못된 JSON 요청)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request: ${e.message}"))
            }
        }
    }

    authenticate("jwt-auth") {
        route("/api/v1/users") {
            /**
             * GET /api/v1/users/me
             * 내 정보 조회
             * - Authorization 헤더의 JWT 토큰을 기반으로 사용자 식별
             */
            get("/me") {
                val userId = call.getUserId()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token subject"))
                    return@get
                }

                val user = userService.getUserById(userId)

                if (user == null) {
                    // 토큰은 유효하지만 DB에 유저가 없는 경우 (탈퇴 직후)
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                } else {
                    call.respond(HttpStatusCode.OK, user)
                }
            }

            /**
             * DELETE /api/v1/users/me
             * 회원 탈퇴
             * - Authorization 헤더의 JWT 토큰을 기반으로 사용자 식별
             */
            delete("/me") {
                val userId = call.getUserId()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token subject"))
                    return@delete
                }

                val success = userService.deleteUser(userId)

                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User deleted successfully"))
                } else {
                    // DB 삭제 실패 (e.g., 존재하지 않는 userI)
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found or delete failed"))
                }
            }
        }
    }
}