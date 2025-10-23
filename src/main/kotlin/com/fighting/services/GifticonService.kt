package com.fighting.services

import com.fighting.db.DatabaseFactory.dbQuery
import com.fighting.db.tables.GifticonTable
import com.fighting.models.Gifticon
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * GifticonService
 * - 기프티콘 CRUD를 담당하는 서비스 계층
 * - 비동기 트랜잭션(dbQuery) 기반
 */
class GifticonService {

    /** ResultRow → Gifticon 매핑 함수 */
    private fun rowToGifticon(row: ResultRow): Gifticon = Gifticon(
        gifticonId = row[GifticonTable.gifticonId],
        userId = row[GifticonTable.userId],
        storeName = row[GifticonTable.storeName],
        expirationDate = row[GifticonTable.expirationDate],
        isUsed = row[GifticonTable.isUsed]
    )

    /** 기프티콘 전체 조회 (관리용) */
    suspend fun getAllGifticons(): List<Gifticon> = dbQuery {
        GifticonTable.selectAll().map(::rowToGifticon)
    }

    /** 특정 유저의 기프티콘 목록 조회 */
    suspend fun getGifticonsByUser(userId: Int): List<Gifticon> = dbQuery {
        GifticonTable
            .select(GifticonTable.userId eq userId)
            .map(::rowToGifticon)
    }

    /** 단일 기프티콘 조회 */
    suspend fun getGifticonById(gifticonId: Int): Gifticon? = dbQuery {
        GifticonTable
            .select(GifticonTable.gifticonId eq gifticonId)
            .map(::rowToGifticon)
            .singleOrNull()
    }

    /** 기프티콘 추가 */
    suspend fun createGifticon(gifticon: Gifticon): Gifticon = dbQuery {
        val insertStatement = GifticonTable.insert {
            it[userId] = gifticon.userId
            it[storeName] = gifticon.storeName
            it[expirationDate] = gifticon.expirationDate
            it[isUsed] = gifticon.isUsed
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::rowToGifticon)
            ?: throw IllegalStateException("기프티콘 생성 실패")
    }

    /** 기프티콘 수정 */
    suspend fun updateGifticon(gifticonId: Int, updatedGifticon: Gifticon): Boolean = dbQuery {
        val updatedRows = GifticonTable.update({ GifticonTable.gifticonId eq gifticonId }) {
            it[userId] = updatedGifticon.userId
            it[storeName] = updatedGifticon.storeName
            it[expirationDate] = updatedGifticon.expirationDate
            it[isUsed] = updatedGifticon.isUsed
        }
        updatedRows > 0
    }

    /** 기프티콘 사용 처리 */
    suspend fun markGifticonAsUsed(gifticonId: Int): Boolean = dbQuery {
        val updatedRows = GifticonTable.update({ GifticonTable.gifticonId eq gifticonId }) {
            it[isUsed] = true
        }
        updatedRows > 0
    }

    /** 기프티콘 삭제 */
    suspend fun deleteGifticon(gifticonId: Int): Boolean = dbQuery {
        GifticonTable.deleteWhere { GifticonTable.gifticonId eq gifticonId } > 0
    }
}