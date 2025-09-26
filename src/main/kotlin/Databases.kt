package com.fighting

import io.ktor.server.application.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// Users 객체와 UserService 클래스가 같은 패키지 내에 있으므로 import가 필요 없습니다.
// 만약 다른 패키지에 있다면, 파일 상단에 import com.fighting.UserService와 같은 코드가 필요합니다.

fun Application.configureDatabases(): UserService {
    val config = HikariConfig().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = "jdbc:mysql://localhost:3306/giftguard"
        username = "giftuser"
        password = "0000"
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    val dataSource = HikariDataSource(config)
    val database = Database.connect(dataSource)

    // ⭐⭐⭐ transaction 블록 수정 시작 ⭐⭐⭐
    // 1. 컴파일 오류 해결: Unit 명시
    // 2. 테이블 생성 오류 해결: 파일 이름(`Users.kt`) 대신 테이블 객체 이름(`Users`) 사용
    transaction(database) {
        addLogger(StdOutSqlLogger)

        // Users 객체(UsersSchema.kt에 정의된)를 사용하여 테이블을 생성합니다.
        SchemaUtils.create(Users)

        // 컴파일 오류 방지 (Cannot infer type...)
        Unit
    }
    // ⭐⭐⭐ transaction 블록 수정 완료 ⭐⭐⭐

    return UserService(database)
}