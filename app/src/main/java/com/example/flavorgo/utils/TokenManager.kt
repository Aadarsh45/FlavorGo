package com.example.flavorgo.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton





class TokenManager @Inject constructor(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("flavorgo_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        Log.d("TokenManager", "Saving token: $token")
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        val token = prefs.getString("jwt_token", null)
        Log.d("TokenManager", "Retrieved token: $token")
        return token
    }

    fun clearToken() {
        Log.d("TokenManager", "Clearing token")
        prefs.edit().remove("jwt_token").apply()
    }
    // Add this debug function to your TokenManager
    fun debugTokenStatus() {
        Log.d("TokenDebug", "Token exists: ${getToken() != null}")

    }
}