package com.lazycode.trafficsense.data.remote

import android.util.Log
import com.lazycode.trafficsense.BuildConfig
import com.lazycode.trafficsense.data.models.LoginResponse
import com.lazycode.trafficsense.data.remote.services.MainApiService
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthAuthenticator(
    private val userPreferences: UserPreferences,
    private val authInterceptor: AuthInterceptor
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            coroutineScope {
                val curToken = userPreferences.getAccessToken().firstOrNull()
                Log.i("AuthenticateTraffs", "cur: $curToken")

                if (curToken != null) {
                    try {
                        val newToken = getNewToken()
                        userPreferences.saveAccessToken(newToken.accessToken!!)
                        val updatedToken = userPreferences.getAccessToken().firstOrNull()

                        Log.i("AuthenticateTraffs", "awal: $curToken")
                        Log.i("AuthenticateTraffs", "akhir: $updatedToken")

                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newToken.accessToken}")
                            .build()
                    } catch (e: Exception) {
                        // Handle token refresh failure
                        Log.e("AuthenticateTraffs", "Token refresh failed: ${e.message}")
                        null
                    }
                } else {
                    // Token is not available, initiate the login flow or handle the scenario accordingly
                    Log.i("AuthenticateTraffs", "Token is not available, initiate login flow")
                    null
                }
            }
        }
//        val curTOken = runBlocking { userPreferences.getAccessToken().first() }
//        Log.i("AuthenticateTraffs", "cur: $curTOken")


//        return runBlocking {
//            val newToken = getNewToken()
//
//            userPreferences.saveAccessToken(newToken.accessToken!!)
//            val cratoken = runBlocking { userPreferences.getAccessToken().first() }
//
//            Log.i("AuthenticateTraffs", "awal: $curTOken")
//            Log.i("AuthenticateTraffs", "akhir: $cratoken")
//
//            response.request.newBuilder()
//                .header("Authorization", "Bearer ${newToken.accessToken}")
//                .build()
//        }
    }

    private suspend fun getNewToken(): LoginResponse {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(MainApiService::class.java)
        return service.refreshToken()
    }
}