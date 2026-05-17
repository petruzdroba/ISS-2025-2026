package com.zdroba.multipitchbuddy.dto

data class AuthRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)
