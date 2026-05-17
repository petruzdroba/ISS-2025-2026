package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.dto.SyncResponse

interface ISyncService {

    suspend fun upload(data: ByteArray)

    suspend fun download() : ByteArray
}