package com.lazycode.trafficsense.data.repo

import com.lazycode.trafficsense.BuildConfig
import com.lazycode.trafficsense.data.remote.services.MapPlacesApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class MapPlacesRepository(private val mapPlacesApiService: MapPlacesApiService) {
    suspend fun searchMap(query: String) =
        mapPlacesApiService.getMapPlaces(query, BuildConfig.API_GRAPHOPPER)
}