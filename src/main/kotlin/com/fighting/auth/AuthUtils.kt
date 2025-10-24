package com.fighting.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * ApplicationCall에서 JWT Principal을 추출하여 userId를 반환하는 확장 함수
 * val userId = call.getUserId()
 */
fun ApplicationCall.getUserId(): Int? {
    val principal = this.principal<JWTPrincipal>()
    return principal?.payload?.subject?.toIntOrNull()
}