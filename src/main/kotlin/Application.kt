/*
// Application.kt

package com.fighting

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// DB, Routing 등의 함수를 가져오기 위해 필요할 수 있습니다.
// 이 코드는 configureSerialization() 등이 다른 파일에 정의되어 있다고 가정합니다.
// import com.fighting.plugins.* // 필요하다면 plugins 패키지를 import

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // ⭐ 1. JSON 처리를 가장 먼저 활성화합니다. (415 오류 해결)
    configureSerialization()

    // 2. 이후에 다른 설정을 호출합니다.
    configureHTTP()
    configureMonitoring()
    configureFrameworks()

    // 3. DB 연결 및 서비스 객체 생성
    val userService = configureDatabases()

    // 4. 라우팅 설정
    configureRouting(userService)
}*/

// Application.kt

package com.fighting

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1. JSON 직렬화 설정을 가장 먼저 호출합니다.
    configureSerialization()

    // 2. Ktor 플러그인들을 이곳에서 모두 설치합니다.
    install(DoubleReceive)
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            // 디버깅 목적으로 오류 원인을 노출합니다.
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    // 3. 나머지 모듈화된 설정들을 호출합니다.
    configureHTTP()
    configureMonitoring()
    configureFrameworks()

    // 4. 데이터베이스 연결 및 서비스 객체를 생성합니다.
    val userService = configureDatabases()

    // 5. 라우팅을 설정합니다. (이제 플러그인 설치 코드가 없습니다.)
    configureRouting(userService)
}