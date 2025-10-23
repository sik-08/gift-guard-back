package com.fighting.plugins

import com.fighting.auth.JwtConfig
import io.ktor.server.application.*

fun Application.configureAuth() {
    JwtConfig.configureAuth(this)
}