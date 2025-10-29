package com.fighting.routes

import com.fighting.models.AuthResponse
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import com.fighting.services.UserService
import io.ktor.client.HttpClient
import com.fighting.plugins.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService, httpClient: HttpClient) {
    authenticate("google-oauth"){
        get("login"){
            // 구글 로그인 페이지로 리디렉션
        }

        get("callback"){
            val principal:OAuthAccessTokenResponse.OAuth2? = call.principal()
            if (principal == null){
                call.respondText("OAUTH failed", status = HttpStatusCode.Unauthorized)
                return@get
            }
            val accessToken = principal.accessToken
            val userInfo = fetchGoogleUserInfo(httpClient = httpClient, accessToken = accessToken)

            if (userInfo != null){
                userService.createOrUpdateUser(userInfo.userId, userInfo.email, userInfo.name)
                val token = makeToken(userInfo.userId, userInfo.email)
                call.respond(AuthResponse(token))
            }else{
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        authenticate("jwt-auth") {
            get(""){
                val principal = call.principal<JWTPrincipal>()
                val email = principal?.payload?.getClaim("email")?.asString()
                val user = email?.let { userService.findByEmail(it) }

                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                } else {
                    call.respond(HttpStatusCode.OK, user)
                }
            }
        }

    }
}