// Serialization.kt (또는 해당 파일)

package com.fighting

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
// kotlinx.serialization.json.Json이 필요하다면 import 합니다.
// import kotlinx.serialization.json.Json 

fun Application.configureSerialization() {
    // ⭐⭐ 라우팅 코드를 제거하고, 플러그인 설치 코드를 넣습니다. ⭐⭐
    install(ContentNegotiation) {
        // kotlinx.serialization을 사용하여 JSON 처리를 활성화합니다.
        json()

        // 필요하다면 아래와 같이 Json 객체를 커스터마이징할 수 있습니다.
        // json(Json { 
        //     prettyPrint = true
        //     isLenient = true 
        // })
    }
}