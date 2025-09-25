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
}