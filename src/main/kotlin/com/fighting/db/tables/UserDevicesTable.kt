package com.fighting.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UserDevicesTable : Table("user_devices") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.userId)
    val fcmToken = varchar("fcm_token", 255).uniqueIndex()
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}