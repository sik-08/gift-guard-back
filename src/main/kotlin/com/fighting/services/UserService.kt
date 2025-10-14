package com.fighting.services

import com.fighting.db.DatabaseFactory.dbQuery
import com.fighting.db.tables.UserDevicesTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.Instant

object UserService {
    suspend fun registerOrUpdateDeviceToken(userId: Long, fcmToken: String) {
        dbQuery {
            val existing = UserDevicesTable.select ( UserDevicesTable.fcmToken eq fcmToken ).singleOrNull()

            if (existing != null) {
                UserDevicesTable.update({ UserDevicesTable.fcmToken eq fcmToken }) {
                    it[UserDevicesTable.userId] = userId
                    it[updatedAt] = Instant.now()
                }
            } else {
                UserDevicesTable.insert {
                    it[UserDevicesTable.userId] = userId
                    it[this.fcmToken] = fcmToken
                    it[createdAt] = Instant.now()
                    it[updatedAt] = Instant.now()
                }
            }
        }
    }

    suspend fun getDeviceTokens(userId: Long): List<String> {
        return dbQuery {
            UserDevicesTable.select ( UserDevicesTable.userId eq userId )
                .map { it[UserDevicesTable.fcmToken] }
        }
    }
}