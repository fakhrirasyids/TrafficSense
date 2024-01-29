package com.lazycode.trafficsense.data.remote

import android.util.Log
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { userPreferences.getAccessToken().first() }
        Log.i("Intercccc", "awal: $token")

        val originalRequest = chain.request().newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(originalRequest)
        Log.i(
            "Intercccc", "code: ${
                response.request
            } -> ${response.message}"
        )

        Log.i(
            "Intercccc", "code: ${
                response.headers
            } -> ${response.message}"
        )

        return response
    }
}