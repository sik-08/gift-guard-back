package com.fighting.db.tables

import org.jetbrains.exposed.sql.Table

object UserTable : Table("users") {
    val userId = integer("user_id").autoIncrement()
    val googleId = varchar("google_id", 255).uniqueIndex()
    val email = varchar("email", 255)
    val name = varchar("name", 100).nullable()

    override val primaryKey = PrimaryKey(userId)
}