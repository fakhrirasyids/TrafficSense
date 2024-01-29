package com.lazycode.trafficsense.data.remote.services

import com.lazycode.trafficsense.data.models.LoginResponse
import com.lazycode.trafficsense.data.models.LogoutResponse
import com.lazycode.trafficsense.data.models.MapPlacesResponse
import com.lazycode.trafficsense.data.models.NavigatorResponse
import com.lazycode.trafficsense.data.models.SensorResponse
import retrofit2.http.*

interface MapPlacesApiService {
    @GET("geocode")
    suspend fun getMapPlaces(
        @Query("q") q: String,
        @Query("key") apiKey: String,
    ): MapPlacesResponse
}