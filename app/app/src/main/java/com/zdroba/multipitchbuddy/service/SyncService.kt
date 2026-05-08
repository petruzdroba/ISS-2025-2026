package com.zdroba.multipitchbuddy.service

import android.util.Base64
import com.zdroba.multipitchbuddy.dto.SyncRequest
import com.zdroba.multipitchbuddy.network.SyncApi
import com.zdroba.multipitchbuddy.network.TokenDataStore

class SyncService(
    private val tokenDataStore: TokenDataStore,
    private val api: SyncApi
): ISyncService {
    private suspend fun bearerToken(): String {
        val token = tokenDataStore.getAccessToken()
            ?: throw IllegalStateException("No access token")
        return "Bearer $token"
    }

    override suspend fun upload(data: ByteArray) {
        api.upload(token = bearerToken(), request = SyncRequest(data))
    }

    override suspend fun download(): ByteArray {
        val response = api.download(token = bearerToken())
        return Base64.decode(response.data, Base64.DEFAULT)
    }
}