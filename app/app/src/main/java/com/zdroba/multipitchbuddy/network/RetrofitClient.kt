package com.zdroba.multipitchbuddy.network

import com.zdroba.multipitchbuddy.BuildConfig
import com.zdroba.multipitchbuddy.dto.ErrorResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val authApi: AuthApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL + "/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)

    fun parseError(response: retrofit2.Response<*>): ErrorResponse? {
        return try {
            val gson = com.google.gson.Gson()
            val errorBody = response.errorBody()?.string()
            gson.fromJson(errorBody, ErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}