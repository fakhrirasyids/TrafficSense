package com.lazycode.trafficsense.ui.main.ui.scan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.SensorItem
import com.lazycode.trafficsense.data.repo.SensorRepository
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel(private val sensorRepository: SensorRepository) : ViewModel() {

    val sensorList: MutableLiveData<ArrayList<SensorItem>> =
        MutableLiveData<ArrayList<SensorItem>>(arrayListOf())

    private val job = SupervisorJob()
    private var fetchScope = CoroutineScope(Dispatchers.Main + job)

    init {
        getSensor()
    }

    fun getSensor() {
        fetchScope.launch {
            sensorRepository.getSensor().collect { result ->
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

    fun pauseJob() {
        Log.e("Sensors", "pauseJob: CEK")
        job.cancelChildren()
    }
}