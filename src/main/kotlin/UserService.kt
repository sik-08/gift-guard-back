package com.fighting

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable

// ⭐ 1. DTO (Data Transfer Object) 정의
@Serializable
data class ExposedUser(
    val id: Int? = null,
    val name: String,
    val email: String
)

class UserService(private val database: Database) {
    fun create(user: ExposedUser): Int = transaction(database) {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
        }[Users.id]
    }

    fun read(id: Int): ExposedUser? = transaction(database) {
        Users.select (Users.id eq id ).singleOrNull()?.let {
            ExposedUser(
                id = it[Users.id],
                name = it[Users.name],
                email = it[Users.email]
            )
        }
    }

    fun readAll(): List<ExposedUser> = transaction(database) {
        Users.selectAll().map {
            ExposedUser(
                id = it[Users.id],
                name = it[Users.name],
                email = it[Users.email]
            )
        }
    }

    fun update(id: Int, user: ExposedUser): Int = transaction(database) {
        Users.update({ Users.id eq id }) {
            it[name] = user.name
            it[email] = user.email
        }
    }

    fun delete(id: Int): Int = transaction(database) {
        Users.deleteWhere { Users.id eq id }
    }
}