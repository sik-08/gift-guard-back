package com.fighting.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

/*
 * Google ID Token을 검증, 클라이언트는 Google Sign-In 후
 * 서버에 idToken을 POST
 * 서버는 이 파일의 verifyIdToken으로 검증
 */

object GoogleAuthService {
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val clientId = System.getenv("GOOGLE_CLIENT_ID") ?: ""

    private val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(listOf(clientId))
        .setIssuer("https://accounts.google.com")
        .build()

    data class VerifiedPayload(val googleId: String, val email: String, val name: String?)

    /*
     * idToken 문자열을 받아서 Google이 서명한 토큰인지 확인하고,
     * 정상이라면 사용자 식별자(googleId), email, name을 반환
     * 실패 시 null 반환
     */
    fun verifyIdToken(idTokenString: String): VerifiedPayload? {
        val idToken: GoogleIdToken? = try {
            verifier.verify(idTokenString)
        } catch (e: Exception) {
            null
        }
        return idToken?.payload?.let { payload ->
            val googleId = payload.subject
            val email = payload.email
            val name = payload["name"] as? String
            if (googleId != null && email != null) {
                VerifiedPayload(googleId, email, name)
            } else null
        }
    }
}