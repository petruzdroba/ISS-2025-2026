package com.zdroba.multipitch_server.dto

data class ErrorResponse(
    val status: Int,
    val message: String?,
    val timestamp: Long,
) {}