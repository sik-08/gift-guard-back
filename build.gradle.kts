// build.gradle.kts (수정된 최종 버전)

val exposed_version: String by project
val h2_version: String by project
val koin_version: String by project
val kotlin_version = "2.2.20"
val logback_version: String by project
val ktor_version = "3.3.0" // ⭐ Ktor 버전 일치

plugins {
    kotlin("jvm") version "2.2.20"
    id("io.ktor.plugin") version "3.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
}

group = "com.fighting"
version = "0.0.1"

application {
    mainClass = "com.fighting.ApplicationKt"
}

dependencies {
    // 모든 Ktor 모듈에 버전을 명시하여 버전 충돌을 방지합니다.
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("io.ktor:ktor-server-double-receive:$ktor_version")
    // ⭐ Resources 의존성에 버전을 명확히 명시합니다.
    implementation("io.ktor:ktor-server-resources:$ktor_version")

    // 중복 제거: 아래는 삭제합니다.
    // implementation("io.ktor:ktor-server-resources")

    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Exposed 및 DB
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("mysql:mysql-connector-java:8.0.28")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Koin 및 로깅
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // 테스트
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}