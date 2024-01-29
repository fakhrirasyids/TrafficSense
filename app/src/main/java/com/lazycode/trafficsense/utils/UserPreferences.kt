package com.lazycode.trafficsense.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lazycode.trafficsense.utils.UserPreferences.Companion.userPreferencesName
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = userPreferencesName)

class UserPreferences  constructor(private val dataStore: DataStore<Preferences>) {
    fun getAccessToken() = dataStore.data.map { it[PREF_ACCESS_TOKEN] ?: preferenceDefaultValue }
    fun getUsername() = dataStore.data.map { it[PREF_USERNAME] ?: preferenceDefaultValue }
    fun getEmail() = dataStore.data.map { it[PREF_EMAIL] ?: preferenceDefaultValue }
    fun getPhone() = dataStore.data.map { it[PREF_PHONE] ?: preferenceDefaultValue }
    fun getProfilePic() = dataStore.data.map { it[PREF_PROFILE_PIC] ?: preferenceDefaultValue }

    fun getVerifiedDocument() = dataStore.data.map { it[PREF_VERIFIED_DOCUMENT] ?: false }

    suspend fun savePreferences(
        accesToken: String,
        username: String,
        email: String,
    ) {
        dataStore.edit { prefs ->
            prefs[PREF_ACCESS_TOKEN] = accesToken
            prefs[PREF_USERNAME] = username
            prefs[PREF_EMAIL] = email
        }
    }

    suspend fun savePreferences(
        accesToken: String,
        username: String,
        email: String,
        phone: String,
        profilePic: String
    ) {
        dataStore.edit { prefs ->
            prefs[PREF_ACCESS_TOKEN] = accesToken
            prefs[PREF_USERNAME] = username
            prefs[PREF_EMAIL] = email
            prefs[PREF_PHONE] = phone
            prefs[PREF_PROFILE_PIC] = profilePic
        }
    }

    suspend fun saveAccessToken(
        accesToken: String,
    ) {
        dataStore.edit { prefs ->
            prefs[PREF_ACCESS_TOKEN] = accesToken
        }
    }

//    suspend fun saveVerifiedDocument(
//        isDocumentVeirified: Boolean,
//    ) {
//        dataStore.edit { prefs ->
//            prefs[PREF_VERIFIED_DOCUMENT] = isDocumentVeirified
//        }
//    }
//

    suspend fun savePhoneProfilePic(
        username: String,
        phone: String,
        profilePic: String
    ) {
        dataStore.edit { prefs ->
            prefs[PREF_USERNAME] = username
            prefs[PREF_PHONE] = phone
            prefs[PREF_PROFILE_PIC] = profilePic
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
//        @Volatile
//        private var INSTANCE: UserPreferences? = null

//        fun getInstance(dataStore: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
//            val instance = UserPreferences(dataStore)
//            INSTANCE = instance
//            instance
//        }

        const val userPreferencesName = "userPreferences"

        val PREF_ACCESS_TOKEN = stringPreferencesKey("pref_access_token")
        val PREF_USERNAME = stringPreferencesKey("pref_username")
        val PREF_EMAIL = stringPreferencesKey("pref_email")
        val PREF_PHONE = stringPreferencesKey("pref_phone")
        val PREF_PROFILE_PIC = stringPreferencesKey("pref_profile_pic")

        val PREF_VERIFIED_DOCUMENT = booleanPreferencesKey("pref_verified_document")

        const val preferenceDefaultValue: String = "preferences_default_value"
    }
}