package com.lazycode.trafficsense.data.repo

import com.lazycode.trafficsense.BuildConfig
import com.lazycode.trafficsense.data.remote.services.MapPlacesApiService
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MapPlacesRepository(private val mapPlacesApiService: MapPlacesApiService) {
    suspend fun searchMap(query: String) =
        mapPlacesApiService.getMapPlaces(query)

//    suspend fun searchMapOnce(query: String) =
//        mapPlacesApiService.getMapPlaces(query)

    fun searchMapOnce(query: String) = flow {
        emit(Result.Loading)
        try {
            val updatePriceResponse = mapPlacesApiService.getMapPlaces(query)
            emit(Result.Success(updatePriceResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}