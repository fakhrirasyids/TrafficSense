package com.lazycode.trafficsense.ui.dynamicroute.savedroute

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.PayloadItemHistory
import com.lazycode.trafficsense.data.models.SavedTripItem
import com.lazycode.trafficsense.data.models.StartPayload
import com.lazycode.trafficsense.data.repo.DynamicRoutingRepository
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedRouteViewModel(
    private val dynamicRoutingRepository: DynamicRoutingRepository,
) : ViewModel() {

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading


    private var _savedRoutesList =
        MutableLiveData<ArrayList<SavedTripItem>>(arrayListOf())
    val savedRoutesList: LiveData<ArrayList<SavedTripItem>> =
        _savedRoutesList

    private var _errorText = MutableLiveData<String>("")
    val errorText: LiveData<String> = _errorText


    init {
        getSavedRoutes()
    }

    fun getSavedRoutes() {
        viewModelScope.launch(Dispatchers.IO) {
            dynamicRoutingRepository.getSavedRoute().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                            _isLoading.postValue(true)
                            _errorText.postValue("")
                        }

                        is Result.Success -> {
                            _isLoading.postValue(false)
                            _errorText.postValue("")
                            _savedRoutesList.postValue(result.data.payload as ArrayList<SavedTripItem>?)
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

    fun getRouteDetail(id: Int) = dynamicRoutingRepository.getRouteDetail(id).asLiveData()
}