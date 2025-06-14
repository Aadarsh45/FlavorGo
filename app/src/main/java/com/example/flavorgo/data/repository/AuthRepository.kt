package com.example.flavorgo.data.repository

import android.util.Log
import com.example.flavorgo.data.model.AuthResponse
import com.example.flavorgo.data.model.LoginRequest
import com.example.flavorgo.data.model.User
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    val tokenManager: TokenManager
) {

    suspend fun signup(user: User): Resource<AuthResponse> {
        return try {
            Log.d("AuthRepository", "Sending SignUp Request: $user")  // Log request payload
            val response = apiService.signUp(user)
            Log.d("AuthRepository", "SignUp Response: $response")  // Log response
            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignUp Error: ${e.message}")
            Resource.Error(e.message ?: "An error occurred")
        }
    }


    suspend fun signin(loginRequest: LoginRequest): Resource<AuthResponse> {
        return try {
            val response = apiService.signIn(loginRequest)
            Log.d("AuthRepository", "Token received: ${response.jwt}")

            // Save token
            response.jwt?.let { jwt ->
                tokenManager.saveToken(jwt)
                Log.d("AuthRepository", "Token saved: ${tokenManager.getToken()}") // Verify
            }

            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign-in error", e)
            Resource.Error(e.message ?: "Unknown error")
        }
    }

}