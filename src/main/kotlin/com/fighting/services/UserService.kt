package com.fighting.services

import com.fighting.db.DatabaseFactory.dbQuery
import com.fighting.db.tables.UserTable
import com.fighting.db.tables.GifticonTable
import com.fighting.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/*
 * UserService:
 * - 사용자 CRUD 및 구글 로그인 관련 로직
 */
class UserService {

    // ResultRow → User 변환
    private fun rowToUser(row: ResultRow): User = User(
        userId = row[UserTable.userId],
        googleId = row[UserTable.googleId],
        email = row[UserTable.email],
        name = row[UserTable.name]
    )

    // 모든 사용자 조회
    suspend fun getAllUsers(): List<User> = dbQuery {
        UserTable.selectAll().map(::rowToUser)
    }

    // ID로 사용자 조회
    suspend fun getUserById(userId: Int): User? = dbQuery {
        UserTable.select(UserTable.userId eq userId).map(::rowToUser).singleOrNull()
    }

    // 구글 ID로 사용자 조회
    suspend fun findByGoogleId(googleId: String): User? = dbQuery {
        UserTable.select(UserTable.googleId eq googleId).map(::rowToUser).singleOrNull()
    }

    // 구글 로그인 기반 사용자 생성 또는 갱신
    suspend fun createOrUpdateFromGoogle(
        googleId: String,
        email: String,
        name: String?
    ): User = dbQuery {
        val existing = UserTable.select(UserTable.googleId eq googleId).singleOrNull()

        if (existing == null) {
            val inserted = UserTable.insert {
                it[UserTable.googleId] = googleId
                it[UserTable.email] = email
                it[UserTable.name] = name
            }.resultedValues?.singleOrNull()

            inserted?.let { rowToUser(it) }
                ?: throw IllegalStateException("User insert failed")
        } else {
            UserTable.update({ UserTable.googleId eq googleId }) {
                it[UserTable.name] = name
                it[UserTable.email] = email
            }
            rowToUser(existing)
        }
    }

    // 사용자 수정
    suspend fun updateUser(userId: Int, name: String?, email: String?): Boolean = dbQuery {
        val updated = UserTable.update({ UserTable.userId eq userId }) {
            if (name != null) it[UserTable.name] = name
            if (email != null) it[UserTable.email] = email
        }
        updated > 0
    }

    // 사용자 삭제
    suspend fun deleteUser(userId: Int): Boolean = dbQuery {
        // 사용자와 연결된 모든 기프티콘을 먼저 삭제
        GifticonTable.deleteWhere { GifticonTable.userId eq userId }

        // 기프티콘 삭제 후, 사용자를 삭제
        val deletedRows = UserTable.deleteWhere { UserTable.userId eq userId }

        // 사용자 삭제가 성공했는지 여부를 반환 (1줄 이상 삭제됨)
        deletedRows > 0
    }
}