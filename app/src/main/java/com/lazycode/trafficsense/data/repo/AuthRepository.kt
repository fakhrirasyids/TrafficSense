package com.lazycode.trafficsense.data.repo

import androidx.lifecycle.liveData
import com.lazycode.trafficsense.data.remote.services.AuthApiService
import com.lazycode.trafficsense.data.remote.services.MainApiService
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AuthRepository(
    private val authApiService: AuthApiService,
    private val mainApiService: MainApiService,
) {
    fun loginUser(username: String, password: String) = flow {
        emit(Result.Loading)
        try {
            val loginUser = authApiService.loginUser(username, password)
            emit(Result.Success(loginUser))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun registerUser(
        fullname: String,
        email: String,
        password: String,
    ) = flow {
        emit(Result.Loading)
        try {
            val registerUser = authApiService.registerUser(fullname, email, password)
            emit(Result.Success(registerUser))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun logoutUser() = flow {
        emit(Result.Loading)
        try {
            val logoutResponse = mainApiService.logoutUser()
            emit(Result.Success(logoutResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

    fun updateProfile(name: String, phoneNumber: String, image: File) = flow {
        emit(Result.Loading)
        try {
            val nameMultipart = name.toRequestBody("text/plain".toMediaType())
            val phoneNumberMultipart = phoneNumber.toRequestBody("text/plain".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData(
                "profile_picture",
                image.name,
                image.asRequestBody("image/jpeg".toMediaTypeOrNull()),
            )
            val updateProfileResponse = mainApiService.updateProfile(
                nameMultipart, phoneNumberMultipart, imageMultipart
            )

            emit(Result.Success(updateProfileResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)
}