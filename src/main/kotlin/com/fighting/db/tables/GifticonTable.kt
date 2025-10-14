package com.fighting.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object GifticonTable : Table("gifticons") {
    val gifticonId = long("gifticon_id").autoIncrement()
    val userId = long("user_id").references(UserTable.userId)
    val storeName = varchar("store_name", 255)
    val productName = varchar("product_name", 255).nullable()
    val expirationDate = date("expiration_date")
    val isUsed = bool("is_used").default(false)
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(gifticonId)
}