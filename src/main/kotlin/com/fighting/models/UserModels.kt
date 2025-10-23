package com.fighting.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Int? = null,
    val googleId: String,
    val email: String,
    val name: String?
)

// 구글 ID 토큰을 받기 위한 요청 DTO
@Serializable
data class GoogleLoginRequest(
    val idToken: String
)

// 로그인 성공 시 JWT 토큰을 반환하기 위한 응답 DTO
@Serializable
data class AuthResponse(
    val token: String
)