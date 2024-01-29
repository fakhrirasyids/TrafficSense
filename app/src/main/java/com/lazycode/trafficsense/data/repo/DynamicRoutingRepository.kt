package com.lazycode.trafficsense.data.repo

import com.lazycode.trafficsense.data.models.StartPayload
import com.lazycode.trafficsense.data.remote.services.MainApiService
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DynamicRoutingRepository(
    private val mainApiService: MainApiService
) {
    fun navigateDestination(
        departureLatitude: String,
        departureLongitude: String,
        destinationLatitude: String,
        destinationLongitude: String,
    ) = flow {
        emit(Result.Loading)
        try {
            val navigateResponse = mainApiService.navigateDestination(
                departureLongitude,
                departureLatitude,
                destinationLongitude,
                destinationLatitude
            )
            emit(Result.Success(navigateResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun startRoute(
        startPayload: StartPayload
    ) = flow {
        emit(Result.Loading)
        try {
            val startResponse = mainApiService.startRoute(
                startPayload
            )
            emit(Result.Success(startResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getSavedRoute(
    ) = flow {
        emit(Result.Loading)
        try {
            val savedTripsResponse = mainApiService.getSavedTrips(
            )
            emit(Result.Success(savedTripsResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getRouteDetail(id: Int) = flow {
        emit(Result.Loading)
        try {
            val routeDetailResponse = mainApiService.getRouteDetail(
                id
            )
            emit(Result.Success(routeDetailResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}