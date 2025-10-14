package com.fighting.db

import com.fighting.db.tables.GifticonTable
import com.fighting.db.tables.UserDevicesTable
import com.fighting.db.tables.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // !! 중요 !!: 이 정보들은 환경 변수나 외부 설정 파일을 통해 관리하는 것이 안전합니다.
        val jdbcURL = "jdbc:mysql://localhost:3306/giftguard"
        val user = "giftuser"
        val password = "0000" // 환경설정 단계에서 만든 DB 비밀번호로 변경

        val hikariConfig = HikariConfig().apply {
            this.driverClassName = "com.mysql.cj.jdbc.Driver"
            this.jdbcUrl = jdbcURL
            this.username = user
            this.password = password
            this.maximumPoolSize = 3
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        Database.connect(HikariDataSource(hikariConfig))

        transaction {
            SchemaUtils.create(UserTable, GifticonTable, UserDevicesTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}