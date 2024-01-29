package com.lazycode.trafficsense.ui.profilesettings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lazycode.trafficsense.data.repo.AuthRepository
import com.lazycode.trafficsense.utils.UserPreferences
import kotlinx.coroutines.launch
import java.io.File

class ProfileSettingsViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val imageProfile = MutableLiveData<File>()

    fun updateProfile(name: String, phoneNumber: String) =
        authRepository.updateProfile(name, phoneNumber, imageProfile.value!!).asLiveData()

    fun logout() =
        authRepository.logoutUser().asLiveData()

    fun clearPreferences() {
        viewModelScope.launch {
            userPreferences.clearPreferences()
        }
    }

    fun savePhoneProfilePic(
        fullname: String,
        phone: String,
        profilePic: String,
    ) {
        viewModelScope.launch {
            userPreferences.savePhoneProfilePic(
                fullname,
                phone,
                profilePic,
            )
        }
    }

    fun getProfilePic() = userPreferences.getProfilePic().asLiveData()
    fun getFullname() = userPreferences.getUsername().asLiveData()
    fun getEmail() = userPreferences.getEmail().asLiveData()
    fun getPhone() = userPreferences.getPhone().asLiveData()
}