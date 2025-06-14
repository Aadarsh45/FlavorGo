package com.example.flavorgo.data.repository

import com.example.flavorgo.data.model.Category
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject


class CategoryRepository @Inject constructor(
    private val apiService: ApiService,
    val tokenManager: TokenManager) {
        suspend fun getCategories(): Resource<List<Category>> {
            return try {
            // Get the token from TokenManager
                 val token = tokenManager.getToken()

            // Check if the token is available
                 if (token == null) {
                    return Resource.Error("Authentication token not found")
                    }

            // Add "Bearer " prefix to the token
            val bearerToken = "Bearer $token"

            // Make the API call with the Bearer token
                 val response = apiService.getCategories(bearerToken)
                 Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
