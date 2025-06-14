package com.flavorgo.data.repository

import com.example.flavorgo.data.model.Category
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.data.model.Restaurant
import com.example.flavorgo.network.ApiService

import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apiService: ApiService,
                                         private val tokenManager: TokenManager) {

    // Function to get categories with authorization token
    suspend fun getCategories(): List<Category> {
        val token = tokenManager.getToken()  // Retrieve the token using TokenManager
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is missing")  // Handle missing token scenario
        }
        return apiService.getCategories("Bearer $token")  // Pass the token in the header
    }

    // Function to get restaurants with authorization token
    suspend fun getRestaurants(): List<Restaurant> {
        val token = tokenManager.getToken()  // Retrieve the token using TokenManager
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is missing")  // Handle missing token scenario
        }
        return apiService.getRestaurants("Bearer $token")  // Pass the token in the header
    }

    // Function to get foods with authorization token
    suspend fun getFoods(): List<Food> {
        val token = tokenManager.getToken()  // Retrieve the token using TokenManager
        if (token.isNullOrEmpty()) {
            throw Exception("Authentication token is missing")  // Handle missing token scenario
        }
        return apiService.getFoods("Bearer $token")  // Pass the token in the header
    }
}

