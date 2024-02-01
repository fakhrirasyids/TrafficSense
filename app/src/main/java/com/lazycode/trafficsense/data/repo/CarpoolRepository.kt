package com.lazycode.trafficsense.data.repo

import com.lazycode.trafficsense.data.remote.services.MainApiService
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CarpoolRepository(
    private val mainApiService: MainApiService
) {
    fun getDocuments() = flow {
        emit(Result.Loading)
        try {
            val documentsResponse = mainApiService.getDocuments()
            emit(Result.Success(documentsResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun storeDocuments(ktpImage: File, simImage: File, stnkImage: File) = flow {
        emit(Result.Loading)
        try {

            val ktpImageMultipart = MultipartBody.Part.createFormData(
                "documents[0][image]",
                ktpImage.name,
                ktpImage.asRequestBody("image/jpeg".toMediaTypeOrNull()),
            )
            val simImageMultipart = MultipartBody.Part.createFormData(
                "documents[1][image]",
                simImage.name,
                simImage.asRequestBody("image/jpeg".toMediaTypeOrNull()),
            )
            val stnkImageMutilpart = MultipartBody.Part.createFormData(
                "documents[2][image]",
                stnkImage.name,
                stnkImage.asRequestBody("image/jpeg".toMediaTypeOrNull()),
            )

            val storeDocumentsResponse = mainApiService.storeDocuments(
                ktpImage = ktpImageMultipart,
                simImage = simImageMultipart,
                stnkImage = stnkImageMutilpart
            )

            emit(Result.Success(storeDocumentsResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getVehicles() = flow {
        emit(Result.Loading)
        try {
            val vehiclesResponse = mainApiService.getVehicles()
            emit(Result.Success(vehiclesResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun storeVehicle(name: String, capacity: String, vehicleImage: File) = flow {
        emit(Result.Loading)
        try {
            val vehicleImageMultipart = MultipartBody.Part.createFormData(
                "vehicle_images[0][image]",
                vehicleImage.name,
                vehicleImage.asRequestBody("image/jpeg".toMediaTypeOrNull()),
            )

            val storeDocumentsResponse = mainApiService.storeVehicle(
                name = name.toRequestBody(),
                capacity = capacity.toRequestBody(),
                vehicleImages = vehicleImageMultipart
            )

            emit(Result.Success(storeDocumentsResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteVehicles(id: Int) = flow {
        emit(Result.Loading)
        try {
            val deleteVehicles = mainApiService.deleteVehicle(id)
            emit(Result.Success(deleteVehicles))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getAllCarpooling() = flow {
        emit(Result.Loading)
        try {
            val allCarpoolingResponse = mainApiService.getAllCarpooling()
            emit(Result.Success(allCarpoolingResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getMyCarpooling() = flow {
        emit(Result.Loading)
        try {
            val myCarpoolingResponse = mainApiService.getMyCarpooling()
            emit(Result.Success(myCarpoolingResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun storeCarpooling(
        departureLatitude: String,
        departureLongitude: String,
        destinationLatitude: String,
        destinationLongitude: String,
        capacity: Int,
        phoneNumber: String,
        departureInfo: String,
        arriveInfo: String,
        vehicleId: Int,
        departureAt: String
    ) = flow {
        emit(Result.Loading)
        try {
            val storeCarpoolingResponse = mainApiService.storeCarpooling(
                departureLatitude,
                departureLongitude,
                destinationLatitude,
                destinationLongitude,
                capacity, phoneNumber, departureInfo, arriveInfo, vehicleId, departureAt
            )
            emit(Result.Success(storeCarpoolingResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getDriverPassengers(id: Int) = flow {
        emit(Result.Loading)
        try {
            val driverPassengerResponse = mainApiService.getDriverPassengers(id)
            emit(Result.Success(driverPassengerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun updatePassengerPrice(id: Int, passengerId: Int, price: Int) = flow {
        emit(Result.Loading)
        try {
            val updatePriceResponse = mainApiService.updatePassengerPrice(id, passengerId, price)
            emit(Result.Success(updatePriceResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun applyPassenger(
        carpoolId: Int,
        departureLatitude: String,
        departureLongitude: String,
        destinationLatitude: String,
        destinationLongitude: String,
        departureInfo: String,
        arriveInfo: String,
        phoneNumber: String,
        passageCount: Int,
    ) = flow {
        emit(Result.Loading)
        try {
            val applyPassengerResponse = mainApiService.applyPassenger(
                carpoolId,
                departureLatitude,
                departureLongitude,
                destinationLatitude,
                destinationLongitude,
                departureInfo, arriveInfo, phoneNumber, passageCount
            )
            emit(Result.Success(applyPassengerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun getHistoryPassengers() = flow {
        emit(Result.Loading)
        try {
            val historyPassengersResponse = mainApiService.getHistoryPassengers()
            emit(Result.Success(historyPassengersResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun updatePassengerStatusDeal(id: Int, passengerId: Int) = flow {
        emit(Result.Loading)
        try {
            val updatePriceResponse = mainApiService.updatePassengerStatusDeal(id, passengerId)
            emit(Result.Success(updatePriceResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun updateCarpoolStatus(id: Int, status: Int) = flow {
        emit(Result.Loading)
        try {
            val carpoolStatusResponse = mainApiService.updateCarpoolStatus(id, status)
            emit(Result.Success(carpoolStatusResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}