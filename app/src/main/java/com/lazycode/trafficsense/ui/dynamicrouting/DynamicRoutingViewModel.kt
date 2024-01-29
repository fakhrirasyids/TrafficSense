package com.lazycode.trafficsense.ui.dynamicrouting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.PathsItem
import com.lazycode.trafficsense.data.repo.AuthRepository
import com.lazycode.trafficsense.data.repo.DynamicRoutingRepository
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.launch

class DynamicRoutingViewModel(
    private val dynamicRoutingRepository: DynamicRoutingRepository,
) : ViewModel() {
    val isPickedDeparture = MutableLiveData<Boolean>(false)
    val isPickedDestination = MutableLiveData<Boolean>(false)

    val departureLat = MutableLiveData(0.0)
    val departureLon = MutableLiveData(0.0)
    val destinationLat = MutableLiveData(0.0)
    val destinationLon = MutableLiveData(0.0)

    val pathsList = MutableLiveData<ArrayList<PathsItem>>(arrayListOf())

    fun navigateDestination() =
        dynamicRoutingRepository.navigateDestination(
            departureLon.value.toString(),
            departureLat.value.toString(),
            destinationLon.value.toString(),
            destinationLat.value.toString(),
        ).asLiveData()
}