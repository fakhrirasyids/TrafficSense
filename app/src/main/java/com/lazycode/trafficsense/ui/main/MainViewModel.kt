package com.lazycode.trafficsense.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.repo.AuthRepository
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun logout() =
        authRepository.logoutUser().asLiveData()

    fun clearPreferences() {
        viewModelScope.launch {
            userPreferences.clearPreferences()
        }
    }
}