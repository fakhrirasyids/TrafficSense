package com.lazycode.trafficsense.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.repo.AuthRepository
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun loginUser(username: String, password: String) =
        authRepository.loginUser(username, password).asLiveData()

    fun registerUser(fullname: String, email: String, password: String) =
        authRepository.registerUser(fullname, email, password).asLiveData()

    fun savePreferences(
        accessToken: String,
        fullname: String,
        email: String,
    ) {
        viewModelScope.launch {
            userPreferences.savePreferences(
                accessToken,
                fullname,
                email,
            )
        }
    }

    fun savePreferences(
        accessToken: String,
        fullname: String,
        email: String,
        phone: String,
        profilePic: String,
        ) {
        viewModelScope.launch {
            userPreferences.savePreferences(
                accessToken,
                fullname,
                email,
                phone, profilePic
            )
        }
    }

//    fun saveVerifiedDocument(
//        isDocumentVerified: Boolean,
//    ) {
//        viewModelScope.launch {
//            userPreferences.saveVerifiedDocument(
//                isDocumentVerified
//            )
//        }
//    }
}