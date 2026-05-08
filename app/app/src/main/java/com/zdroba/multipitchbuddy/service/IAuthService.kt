package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.dto.AuthResponse
import com.zdroba.multipitchbuddy.dto.UserDto

interface IAuthService {

    suspend fun register(email:String, username:String, password:String, rememberMe: Boolean=false): AuthResponse

    suspend fun login(email: String, password: String, rememberMe: Boolean=false): AuthResponse

    suspend fun refresh(): AuthResponse

    suspend fun me(): UserDto

    suspend fun logout()

    suspend fun isLoggedIn(): Boolean
}