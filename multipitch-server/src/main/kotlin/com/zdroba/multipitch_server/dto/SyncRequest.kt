package com.zdroba.multipitch_server.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class SyncRequest(
    @field:NotNull
    @field:Size(min = 1)
    val data: ByteArray
)
