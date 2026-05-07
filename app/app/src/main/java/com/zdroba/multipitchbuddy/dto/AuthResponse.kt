package com.zdroba.multipitchbuddy.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String?,
    val user: UserDto
)
