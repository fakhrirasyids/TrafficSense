package com.lazycode.trafficsense.ui.carpool

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.models.CarpoolingItem
import com.lazycode.trafficsense.data.repo.CarpoolRepository
import com.lazycode.trafficsense.utils.Result
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class CarpoolViewModel(
    private val carpoolRepository: CarpoolRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun phoneNumberOnce() = runBlocking { userPreferences.getPhone().first() }

    val ktpImage = MutableLiveData<File>()
    val simImage = MutableLiveData<File>()
    val stnkImage = MutableLiveData<File>()

    fun getProfilePic() = userPreferences.getProfilePic().asLiveData()
    fun getFullname() = userPreferences.getUsername().asLiveData()
    fun getPhone() = userPreferences.getPhone().asLiveData()

    fun getDocuments() = carpoolRepository.getDocuments().asLiveData()
    fun storeDocuments() = carpoolRepository.storeDocuments(
        ktpImage.value!!,
        simImage.value!!,
        stnkImage.value!!
    ).asLiveData()

    init {
        getCarpoolingData()
    }

    fun getCarpoolingData() {
        getAllCarpool()
        getMyCarpool()
    }

    private var _isLoadingAllCarpool = MutableLiveData<Boolean>(false)
    val isLoadingAllCarpool: LiveData<Boolean> = _isLoadingAllCarpool

    private var _allCarpoolList = MutableLiveData<ArrayList<CarpoolingItem>>(arrayListOf())
    val allCarpoolList: LiveData<ArrayList<CarpoolingItem>> = _allCarpoolList

    private var _isLoadingMyCarpool = MutableLiveData<Boolean>(false)
    val isLoadingMyCarpool: LiveData<Boolean> = _isLoadingMyCarpool

    private var _myCarpoolList = MutableLiveData<ArrayList<CarpoolingItem>>(arrayListOf())
    val myCarpoolList: LiveData<ArrayList<CarpoolingItem>> = _myCarpoolList

    private var _errorTextAllCarpool = MutableLiveData<String>("")
    val errorTextAllCarpoolingItem: LiveData<String> = _errorTextAllCarpool

    private var _errorTextMyCarpool = MutableLiveData<String>("")
    val errorTextMyCarpoolingItem: LiveData<String> = _errorTextMyCarpool

    private fun getAllCarpool() {
        viewModelScope.launch(Dispatchers.IO) {
            carpoolRepository.getAllCarpooling().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                            _isLoadingAllCarpool.postValue(true)
                            _errorTextAllCarpool.postValue("")
                        }

                        is Result.Success -> {
                            _isLoadingAllCarpool.postValue(false)
                            _errorTextAllCarpool.postValue("")
                            _allCarpoolList.postValue(result.data.payload!! as ArrayList<CarpoolingItem>?)
                            Log.e("KONTOL", "SORRRTATT: ${result.data.payload.size}", )

                        }

                        is Result.Error -> {
                            _isLoadingAllCarpool.postValue(false)
                            _errorTextAllCarpool.postValue(result.error)
                        }
                    }
                }
            }
        }
    }

    private fun getMyCarpool() {
        viewModelScope.launch(Dispatchers.IO) {
            carpoolRepository.getMyCarpooling().collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is Result.Loading -> {
                            _isLoadingMyCarpool.postValue(true)
                            _errorTextMyCarpool.postValue("")
                        }

                        is Result.Success -> {
                            _isLoadingMyCarpool.postValue(false)
                            _errorTextMyCarpool.postValue("")
                            _myCarpoolList.postValue(result.data.payload!! as ArrayList<CarpoolingItem>?)
                        }

                        is Result.Error -> {
                            _isLoadingMyCarpool.postValue(false)
                            _errorTextMyCarpool.postValue(result.error)
                        }
                    }
                }
            }
        }
    }

    fun applyPassenger(
        carpoolId: Int,
        latDeparture: Double?,
        lonDeparture: Double?,
        latDestination: Double?,
        lonDestination: Double?,
        departureInfo: String,
        arriveInfo: String,
        capacity: Int,
    ) = carpoolRepository.applyPassenger(
        carpoolId,
        lonDeparture.toString(),
        latDeparture.toString(),
        lonDestination.toString(),
        latDestination.toString(),
        departureInfo,
        arriveInfo,
        runBlocking { userPreferences.getPhone().first() },
        capacity
    ).asLiveData()

    fun updateCarpoolStatus(id: Int, status: Int) = carpoolRepository.updateCarpoolStatus(id, status).asLiveData()
}