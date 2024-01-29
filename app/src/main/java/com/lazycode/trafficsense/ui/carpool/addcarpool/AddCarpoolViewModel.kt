package com.lazycode.trafficsense.ui.carpool.addcarpool

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.lazycode.trafficsense.data.repo.CarpoolRepository
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AddCarpoolViewModel(
    private val carpoolRepository: CarpoolRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    val selectedVehicleId = MutableLiveData<Int>(null)

    fun storeCarpool(
        latDeparture: Double?,
        lonDeparture: Double?,
        latDestination: Double?,
        lonDestination: Double?,
        capacity: Int,
        departureInfo: String,
        arriveInfo: String,
        departureAt: String
    ) = carpoolRepository.storeCarpooling(
        lonDeparture.toString(),
        latDeparture.toString(),
        lonDestination.toString(),
        latDestination.toString(),
        capacity,
        runBlocking { userPreferences.getPhone().first() },
        departureInfo,
        arriveInfo,
        selectedVehicleId.value!!,
        departureAt
    ).asLiveData()
}