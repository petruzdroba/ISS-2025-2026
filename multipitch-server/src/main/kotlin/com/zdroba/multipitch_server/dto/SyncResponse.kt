package com.zdroba.multipitch_server.dto

import java.time.LocalDateTime

data class SyncResponse(
    val data: ByteArray,
    val createdAt: LocalDateTime? = null
)
