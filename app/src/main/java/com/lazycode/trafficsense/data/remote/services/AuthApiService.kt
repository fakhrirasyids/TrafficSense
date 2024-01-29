package com.lazycode.trafficsense.data.remote.services

import com.lazycode.trafficsense.data.models.LoginResponse
import com.lazycode.trafficsense.data.models.LogoutResponse
import com.lazycode.trafficsense.data.models.NavigatorResponse
import com.lazycode.trafficsense.data.models.RegisterResponse
import com.lazycode.trafficsense.data.models.SensorResponse
import retrofit2.Call
import retrofit2.http.*

interface AuthApiService {
    @POST("login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("register")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

//    @POST("refresh")
//    suspend fun refreshToken(
//    ): LoginResponse
//
//    @POST("logout")
//    suspend fun logoutUser(
//    ): LogoutResponse
}