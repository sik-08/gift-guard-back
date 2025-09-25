package com.fighting

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    // ⭐⭐⭐ 500 오류 해결을 위해 email 필드 추가 ⭐⭐⭐
    val email = varchar("email", 100).uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}