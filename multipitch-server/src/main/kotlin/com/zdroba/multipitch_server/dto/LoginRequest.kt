package com.zdroba.multipitch_server.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank
    val password: String
)
