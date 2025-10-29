package com.fighting.routes

import com.fighting.models.Gifticon
import com.fighting.models.GifticonRequest
import com.fighting.services.GifticonService
import io.ktor.client.HttpClient
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gifticonRoutes(gifticonService: GifticonService, httpClient: HttpClient) {

}