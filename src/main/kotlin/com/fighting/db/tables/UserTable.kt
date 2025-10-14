package com.fighting.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UserTable : Table("users") {
    val userId = long("user_id").autoIncrement()
    val googleId = varchar("google_id", 255).uniqueIndex()
    val email = varchar("email", 255)
    val displayName = varchar("display_name", 100).nullable()
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("updated_at").default(Instant.now())

    override val primaryKey = PrimaryKey(userId)
}