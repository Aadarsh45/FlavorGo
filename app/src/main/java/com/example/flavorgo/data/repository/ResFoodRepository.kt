package com.example.flavorgo.data.repository

import com.example.flavorgo.data.model.Food
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject

class ResFoodRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun getRestaurantFoods(
        restaurantId: Long,

    ): Resource<List<Food>> {
        return try {
            val token = tokenManager.getToken()
            if (token != null) {
                val response = apiService.getRestaurantFoods(
                    restaurantId = restaurantId,
                    token = "Bearer $token",
//                    vegetarian = vegetarian
                )
                Resource.Success(response.map { foodResponse ->
                    Food(
                        id = foodResponse.id,
                        name = foodResponse.name,
                        description = foodResponse.description,
                        price = foodResponse.price.toDouble(),
                        category = foodResponse.foodCategory?.name.toString(),
                        images = foodResponse.images ?: emptyList(),
                        isVegetarian = foodResponse.vegetarian,
                        isAvailable = foodResponse.available
                    )
                })
            } else {
                Resource.Error("Authentication required")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch food items")
        }
    }
}