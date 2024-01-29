package com.lazycode.trafficsense.utils

class SessionManager {
    // Other session management properties and methods

    private var accessToken: String? = null
    private var accessTokenExpirationTime: Long? = null

    // Method to check if the access token has expired
    fun isAccessTokenExpired(): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        return accessTokenExpirationTime != null && currentTimeMillis >= accessTokenExpirationTime!!
    }

    // Method to update the access token and its expiration time in the session
    fun updateAccessToken(token: String, expiresIn: Long) {
        accessToken = token
        accessTokenExpirationTime = System.currentTimeMillis() + expiresIn * 1000 // Convert expiresIn to milliseconds
    }

    // Other session management methods
}