package com.fighting.db

import com.fighting.db.tables.GifticonTable
import com.fighting.db.tables.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val jdbcURL = System.getenv("DB_NAME")
        val user = System.getenv("DB_USER")
        val password = System.getenv("DB_PASSWORD")
        val driverClassName = System.getenv("DRIVER_CLASS_NAME")

        val hikariConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcURL
            this.username = user
            this.password = password
            this.maximumPoolSize = 3
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val database = Database.connect(HikariDataSource(hikariConfig))

        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(GifticonTable, UserTable)
            SchemaUtils.create(UserTable, GifticonTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}