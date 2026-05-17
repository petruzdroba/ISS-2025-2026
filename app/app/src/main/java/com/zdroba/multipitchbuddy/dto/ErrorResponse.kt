package com.zdroba.multipitchbuddy.dto

data class ErrorResponse(
    val status: Int,
    val message: String?,
    val timestamp: Long
)