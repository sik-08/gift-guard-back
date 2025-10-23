package com.fighting.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * Gifticon 데이터 모델
 *
 * - gifticonId: 기프티콘 고유 ID
 * - userId: 사용자 ID (FK)
 * - storeName: 브랜드명 혹은 매장 이름
 * - expirationDate: 유효기간 (LocalDate)
 * - isUsed: 사용 여부
 */
@Serializable
data class Gifticon(
    val gifticonId: Int? = null,
    val userId: Int,
    val storeName: String,
    @Serializable(with = LocalDateSerializer::class)
    val expirationDate: LocalDate,
    val isUsed: Boolean = false
)

/**
 * 1. 기프티콘 생성/수정 요청 시 클라이언트로부터 받을 데이터 모델 (DTO)
 * - userId는 JWT 토큰에서 추출하므로 받지 않습니다.
 * - gifticonId, isUsed는 서버에서 관리하므로 받지 않습니다.
 */
@Serializable
data class GifticonRequest(
    val storeName: String,
    @Serializable(with = LocalDateSerializer::class)
    val expirationDate: LocalDate
)