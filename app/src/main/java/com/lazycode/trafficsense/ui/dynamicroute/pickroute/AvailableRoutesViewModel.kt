package com.lazycode.trafficsense.ui.dynamicroute.pickroute

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.SensorItem
import com.lazycode.trafficsense.data.models.StartPayload
import com.lazycode.trafficsense.data.repo.DynamicRoutingRepository
import com.lazycode.trafficsense.data.repo.SensorRepository
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AvailableRoutesViewModel(
    private val dynamicRoutingRepository: DynamicRoutingRepository,
    private val sensorRepository: SensorRepository
) : ViewModel() {
    fun startRoute(startPayload: StartPayload) =
        dynamicRoutingRepository.startRoute(startPayload).asLiveData()

    val sensorList: MutableLiveData<ArrayList<SensorItem>> =
        MutableLiveData<ArrayList<SensorItem>>(arrayListOf())

    init {
        getSensorOnce()
    }

    private fun getSensorOnce() {
        viewModelScope.launch(Dispatchers.IO) {
            sensorRepository.getSensorOnce().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                        }

                        is Result.Success -> {
                            sensorList.postValue(result.data.payload as ArrayList<SensorItem>?)
                        }

                        is Result.Error -> {
                        }
                    }
                }
            }
        }
    }
}