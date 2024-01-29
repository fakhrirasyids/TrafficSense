package com.lazycode.trafficsense.data.repo

import com.lazycode.trafficsense.data.remote.services.MainApiService
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive

class SensorRepository(
    private val mainApiService: MainApiService
) {
    fun getSensor() = flow {
        do {
            emit(Result.Loading)

            try {
                val listSensor = mainApiService.getSensor()
                emit(Result.Success(listSensor))
                kotlinx.coroutines.delay(5000)
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

        } while (currentCoroutineContext().isActive)
    }.flowOn(Dispatchers.IO)

    fun getSensorOnce() = flow {
        emit(Result.Loading)

        try {
            val listSensor = mainApiService.getSensor()
            emit(Result.Success(listSensor))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}