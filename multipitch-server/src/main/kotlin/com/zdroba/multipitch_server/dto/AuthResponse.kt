package com.zdroba.multipitch_server.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String?,
    val user: UserDto
)
