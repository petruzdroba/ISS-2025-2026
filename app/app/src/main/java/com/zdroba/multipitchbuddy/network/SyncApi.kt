package com.zdroba.multipitchbuddy.network

import com.zdroba.multipitchbuddy.dto.SyncRequest
import com.zdroba.multipitchbuddy.dto.SyncResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SyncApi {
    @POST("sync")
    suspend fun upload(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    )

    @GET("sync")
    suspend fun download(
        @Header("Authorization") token: String
    ): SyncResponse
}