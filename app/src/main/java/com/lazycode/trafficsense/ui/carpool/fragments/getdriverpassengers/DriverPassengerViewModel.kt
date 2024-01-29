package com.lazycode.trafficsense.ui.carpool.fragments.getdriverpassengers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.DriverPassengerItem
import com.lazycode.trafficsense.data.models.VehicleItem
import com.lazycode.trafficsense.data.repo.CarpoolRepository
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DriverPassengerViewModel(
    private val carpoolRepository: CarpoolRepository,
    private val carpoolId: Int,
) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var _driverPassengerListAccept =
        MutableLiveData<ArrayList<DriverPassengerItem>>(arrayListOf())
    val driverPassengerListAccept: LiveData<ArrayList<DriverPassengerItem>> =
        _driverPassengerListAccept

    private var _driverPassengerListNegotiate =
        MutableLiveData<ArrayList<DriverPassengerItem>>(arrayListOf())
    val driverPassengerListNegotiate: LiveData<ArrayList<DriverPassengerItem>> =
        _driverPassengerListNegotiate

    private var _errorText = MutableLiveData<String>("")
    val errorText: LiveData<String> = _errorText

    init {
        getDriverPassengers()
    }

    fun getDriverPassengers() {
        viewModelScope.launch(Dispatchers.IO) {
            carpoolRepository.getDriverPassengers(carpoolId).collect { result ->
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
                            Log.e("COUS", "ALL ; ${passengers.size}")

                            val acceptedPassengers =
                                passengers.filter { it?.status.toString().toInt() != 1 }
                            val negotiatePassengers =
                                passengers.filter { it?.status.toString().toInt() == 1 }

                            _driverPassengerListAccept.postValue(acceptedPassengers as ArrayList<DriverPassengerItem>?)
                            _driverPassengerListNegotiate.postValue(negotiatePassengers as ArrayList<DriverPassengerItem>?)
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

    fun updatePassengerPrice(passengerId: Int, price: Int) =
        carpoolRepository.updatePassengerPrice(carpoolId, passengerId, price).asLiveData()
}