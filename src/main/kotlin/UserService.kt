/*
package com.fighting

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable // JSON 처리를 위해 추가
import com.fighting.Users

// -----------------------------------------------------
// 1. DTO (Data Transfer Object) 정의: 여기에만 남겨둡니다.
// -----------------------------------------------------

// ExposedUser는 API 요청 데이터 (name, email)와 일치해야 합니다.
@Serializable // Ktor의 ContentNegotiation이 이 클래스를 JSON으로 다룰 수 있도록 해줍니다.
data class ExposedUser(
    val name: String,
    val email: String
)

// -----------------------------------------------------
// 2. UserService: CRUD 로직 구현
// -----------------------------------------------------

class UserService(private val database: Database) {

    // C: Create (새로운 사용자 생성)
    fun create(user: ExposedUser): Int = transaction(database) {
        Users.insert { // ⭐ Users 테이블 객체 사용
            it[Users.name] = user.name
            it[Users.email] = user.email // email 필드 포함
        }[Users.id]
    }

    // R: Read (특정 ID의 사용자 조회)
    fun read(id: Int): ExposedUser? = transaction(database) {
        Users.select ( Users.id eq id ).singleOrNull()?.let {
            ExposedUser(
                name = it[Users.name],
                email = it[Users.email]
            )
        }
    }

    // U: Update (특정 ID의 사용자 정보 수정)
    fun update(id: Int, user: ExposedUser) = transaction(database) {
        Users.update({ Users.id eq id }) { // ⭐ Users 테이블 객체 사용
            it[Users.name] = user.name
            it[Users.email] = user.email // email 필드 포함
        }
    }

    // D: Delete (특정 ID의 사용자 삭제)
    fun delete(id: Int) = transaction(database) {
        Users.deleteWhere { Users.id eq id } // ⭐ Users 테이블 객체 사용
    }
}*/
// UserService.kt

package com.fighting

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.serialization.Serializable

// ⭐ 1. DTO (Data Transfer Object) 정의
@Serializable
data class ExposedUser(
    val id: Int? = null, // id 필드 추가 (Nullable, 기본값 null)
    val name: String,
    val email: String
)

// ⭐ 2. UserService: CRUD 로직 구현
class UserService(private val database: Database) {

    // C: Create (새로운 사용자 생성)
    fun create(user: ExposedUser): Int = transaction(database) {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
        }[Users.id]
    }

    // R: Read (특정 ID의 사용자 조회) - id를 포함하여 반환하도록 수정
    fun read(id: Int): ExposedUser? = transaction(database) {
        Users.select (Users.id eq id ).singleOrNull()?.let {
            ExposedUser(
                id = it[Users.id], // id 필드 추가
                name = it[Users.name],
                email = it[Users.email]
            )
        }
    }

    // 👇👇👇 모든 사용자 조회 함수 추가 👇👇👇
    // R: Read All (모든 사용자 조회)
    fun readAll(): List<ExposedUser> = transaction(database) {
        Users.selectAll().map {
            ExposedUser(
                id = it[Users.id], // id 필드 추가
                name = it[Users.name],
                email = it[Users.email]
            )
        }
    }

    // U: Update (특정 ID의 사용자 정보 수정)
    fun update(id: Int, user: ExposedUser): Int = transaction(database) {
        Users.update({ Users.id eq id }) {
            it[name] = user.name
            it[email] = user.email
        }
    }

    // D: Delete (특정 ID의 사용자 삭제)
    fun delete(id: Int): Int = transaction(database) {
        Users.deleteWhere { Users.id eq id }
    }
}