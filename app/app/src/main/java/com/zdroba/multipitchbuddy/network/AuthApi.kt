package com.zdroba.multipitchbuddy.network

import com.zdroba.multipitchbuddy.dto.AuthRequest
import com.zdroba.multipitchbuddy.dto.AuthResponse
import com.zdroba.multipitchbuddy.dto.RegisterRequest
import com.zdroba.multipitchbuddy.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Header("Authorization") token: String): AuthResponse

    @GET("auth/me")
    suspend fun me(@Header("Authorization") token: String): UserDto
}