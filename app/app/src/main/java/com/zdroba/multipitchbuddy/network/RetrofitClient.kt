package com.zdroba.multipitchbuddy.network

import com.zdroba.multipitchbuddy.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val authApi: AuthApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL + "/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthApi::class.java)
}