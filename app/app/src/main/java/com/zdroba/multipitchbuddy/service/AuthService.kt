package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.dto.AuthRequest
import com.zdroba.multipitchbuddy.dto.AuthResponse
import com.zdroba.multipitchbuddy.dto.RegisterRequest
import com.zdroba.multipitchbuddy.dto.UserDto
import com.zdroba.multipitchbuddy.network.AuthApi
import com.zdroba.multipitchbuddy.network.TokenDataStore

class AuthService(
    private val tokenDataStore: TokenDataStore,
    private val api: AuthApi
) {
    suspend fun register(email: String, username: String, password: String, rememberMe: Boolean): AuthResponse {
        val response = api.register(RegisterRequest(email, username, password, rememberMe))
        tokenDataStore.saveTokens(response.accessToken, response.refreshToken)
        return response
    }

    suspend fun login(email: String, password: String, rememberMe: Boolean = false): AuthResponse {
        val response = api.login(AuthRequest(email, password, rememberMe))
        tokenDataStore.saveTokens(response.accessToken, response.refreshToken)
        return response
    }

    suspend fun refresh(): AuthResponse {
        val refreshToken = tokenDataStore.getRefreshToken() ?: throw Exception("No refresh token")
        val response = api.refresh("Bearer $refreshToken")
        tokenDataStore.saveTokens(response.accessToken, response.refreshToken)
        return response
    }

    suspend fun me(): UserDto {
        val accessToken = tokenDataStore.getAccessToken() ?: throw Exception("Not logged in")
        return api.me("Bearer $accessToken")
    }

    suspend fun logout() = tokenDataStore.clearTokens()

    suspend fun isLoggedIn(): Boolean = tokenDataStore.getAccessToken() != null
}