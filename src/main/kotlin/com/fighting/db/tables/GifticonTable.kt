package com.fighting.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object GifticonTable : Table("gifticons") {
    val gifticonId = integer("gifticon_id").autoIncrement()
    val userId = integer("user_id").references(UserTable.userId)
    val storeName = varchar("store_name", 255)
    val expirationDate = date("expiration_date")
    val isUsed = bool("is_used").default(false)

    override val primaryKey = PrimaryKey(gifticonId)
}