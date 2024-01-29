package com.lazycode.trafficsense.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val USER_TOKEN = "USER_TOKEN"
    private const val REFRESH_TOKEN = "REFRESH_TOKEN"
    private const val IS_LOGGED_IN = "IS_LOGGED_IN"

    private var prefs: SharedPreferences? = null

    fun init(context : Context){
        prefs = context.getSharedPreferences(context.packageName, 0)
    }

    var token: String?
        get() = prefs?.getString(USER_TOKEN, null)
        set(value) {
            prefs?.edit()?.apply {
                putString(USER_TOKEN, value).apply()
                putBoolean(IS_LOGGED_IN, true)
            }
        }

    var refreshToken: String?
        get() = prefs?.getString(REFRESH_TOKEN, null)
        set(value) {
            prefs?.edit()?.putString(REFRESH_TOKEN, value)?.apply()
        }
}