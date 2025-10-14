package com.fighting.services

import com.fighting.db.DatabaseFactory.dbQuery
import com.fighting.db.tables.GifticonTable
import com.fighting.models.GifticonCreateRequest
import com.fighting.models.GifticonResponse
import com.fighting.models.GifticonUpdateRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant

object GifticonService {
    suspend fun create(userId: Long, request: GifticonCreateRequest): GifticonResponse {
        val newId = dbQuery {
            GifticonTable.insert {
                it[this.userId] = userId
                it[storeName] = request.storeName
                it[productName] = request.productName
                it[expirationDate] = request.expirationDate
                it[createdAt] = Instant.now()
                it[updatedAt] = Instant.now()
            } get GifticonTable.gifticonId
        }
        return findById(newId)!!
    }

    suspend fun findByUser(userId: Long): List<GifticonResponse> = dbQuery {
        GifticonTable.select ( GifticonTable.userId eq userId )
            .map(::rowToGifticonResponse)
    }

    private suspend fun findById(id: Long): GifticonResponse? = dbQuery {
        GifticonTable.select ( GifticonTable.gifticonId eq id )
            .map(::rowToGifticonResponse)
            .singleOrNull()
    }

    suspend fun update(id: Long, userId: Long, request: GifticonUpdateRequest): Boolean = dbQuery {
        GifticonTable.update({ (GifticonTable.gifticonId eq id) and (GifticonTable.userId eq userId) }) {
            it[isUsed] = request.isUsed
            it[updatedAt] = Instant.now()
        } > 0
    }

    suspend fun delete(id: Long, userId: Long): Boolean = dbQuery {
        GifticonTable.deleteWhere { (gifticonId eq id) and (GifticonTable.userId eq userId) } > 0
    }

    private fun rowToGifticonResponse(row: ResultRow): GifticonResponse = GifticonResponse(
        gifticonId = row[GifticonTable.gifticonId],
        storeName = row[GifticonTable.storeName],
        productName = row[GifticonTable.productName],
        expirationDate = row[GifticonTable.expirationDate],
        isUsed = row[GifticonTable.isUsed]
    )
}