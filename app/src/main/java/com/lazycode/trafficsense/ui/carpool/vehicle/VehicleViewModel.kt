package com.lazycode.trafficsense.ui.carpool.vehicle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.VehicleItem
import com.lazycode.trafficsense.data.repo.CarpoolRepository
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class VehicleViewModel(
    private val carpoolRepository: CarpoolRepository
) : ViewModel() {
    val vehicleImage = MutableLiveData<File>()

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var _vehicleList = MutableLiveData<ArrayList<VehicleItem>>(arrayListOf())
    val vehicleList: LiveData<ArrayList<VehicleItem>> = _vehicleList

    private var _errorText = MutableLiveData<String>("")
    val errorText: LiveData<String> = _errorText

    init {
        getVehicles()
    }

    fun getVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            carpoolRepository.getVehicles().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                            _isLoading.postValue(true)
                            _errorText.postValue("")
                        }

                        is Result.Success -> {
                            _isLoading.postValue(false)
                            _errorText.postValue("")
                            _vehicleList.postValue(result.data.payload!! as ArrayList<VehicleItem>?)
                        }

                        is Result.Error -> {
                            _isLoading.postValue(false)
                            _errorText.postValue(result.error)
                        }
                    }
                }
            }
        }
    }

    fun storeVehicles(
        name: String,
        capacity: String
    ) = carpoolRepository.storeVehicle(name, capacity,
        Constants.reduceFileSize(vehicleImage.value!!)
    ).asLiveData()

    fun deleteVehicles(
        id: Int
    ) = carpoolRepository.deleteVehicles(id).asLiveData()
}