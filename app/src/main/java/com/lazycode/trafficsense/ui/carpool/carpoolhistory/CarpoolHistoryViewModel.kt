package com.lazycode.trafficsense.ui.carpool.carpoolhistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.PayloadItemHistory
import com.lazycode.trafficsense.data.repo.CarpoolRepository
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarpoolHistoryViewModel(
    private val carpoolRepository: CarpoolRepository
) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var _historyAccepted =
        MutableLiveData<ArrayList<PayloadItemHistory>>(arrayListOf())
    val historyAccepted: LiveData<ArrayList<PayloadItemHistory>> =
        _historyAccepted

    private var _historyNegotiate =
        MutableLiveData<ArrayList<PayloadItemHistory>>(arrayListOf())
    val historyNegotiate: LiveData<ArrayList<PayloadItemHistory>> =
        _historyNegotiate

    private var _errorText = MutableLiveData<String>("")
    val errorText: LiveData<String> = _errorText

    init {
        getHistoryPassengers()
    }

    fun getHistoryPassengers() {
        viewModelScope.launch(Dispatchers.IO) {
            carpoolRepository.getHistoryPassengers().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                            _isLoading.postValue(true)
                            _errorText.postValue("")
                        }

                        is Result.Success -> {
                            _isLoading.postValue(false)
                            _errorText.postValue("")

                            val passengers = result.data.payload!!

                            val acceptedPassengers =
                                passengers.filter { it?.status.toString().toInt() != 1 }
                            val negotiatePassengers =
                                passengers.filter { it?.status.toString().toInt() == 1 }

                            _historyAccepted.postValue(acceptedPassengers as ArrayList<PayloadItemHistory>?)
                            _historyNegotiate.postValue(negotiatePassengers as ArrayList<PayloadItemHistory>?)
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

    fun updateStatusDeal(carpoolingId: Int, passengerId: Int) =
        carpoolRepository.updatePassengerStatusDeal(carpoolingId, passengerId).asLiveData()
}