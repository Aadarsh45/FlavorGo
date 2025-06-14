package com.example.flavorgo.data.repository

import android.util.Log
import com.example.flavorgo.data.model.Restaurant
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject

class RestaurantRepository @Inject constructor(private val apiService: ApiService,
                                               private val tokenManager: TokenManager) {
    suspend fun getRestaurants(): Resource<List<Restaurant>> {
        return try {
            val token = tokenManager.getToken()

            if (token.isNullOrEmpty()) {
                Log.e("RestaurantRepository", "No authentication token found")
                return Resource.Error("Authentication token not found")
            }

            val bearerToken = "Bearer $token"
            Log.d("RestaurantRepository", "Fetching restaurants with token: $bearerToken")

            val response = apiService.getRestaurants(bearerToken)

            Log.d("RestaurantRepository", "API Response: $response")

            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "API Error: ${e.message}")
            Resource.Error(e.message ?: "An error occurred")
        }
    }

}