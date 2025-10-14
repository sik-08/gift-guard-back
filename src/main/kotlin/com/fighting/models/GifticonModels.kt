package com.fighting.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class GifticonResponse(
    val gifticonId: Long,
    val storeName: String,
    val productName: String?,
    @Serializable(with = LocalDateSerializer::class)
    val expirationDate: LocalDate,
    val isUsed: Boolean
)

@Serializable
data class GifticonCreateRequest(
    val storeName: String,
    val productName: String?,
    @Serializable(with = LocalDateSerializer::class)
    val expirationDate: LocalDate
)

@Serializable
data class GifticonUpdateRequest(
    val isUsed: Boolean
)