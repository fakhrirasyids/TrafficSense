package com.lazycode.trafficsense.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.lazycode.trafficsense.utils.UserPreferences

class SplashViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun getAccessToken() = userPreferences.getAccessToken().asLiveData()
}