package com.fighting.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Long,
    val email: String,
    val displayName: String?
)

@Serializable
data class GoogleLoginRequest(val idToken: String)

@Serializable
data class AuthResponse(val accessToken: String)

// 로컬 테스트용
@Serializable
data class LocalLoginRequest(
    val email: String,
    val displayName: String?
)