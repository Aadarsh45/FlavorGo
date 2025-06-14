package com.example.flavorgo.network

import com.example.flavorgo.data.model.Category
import com.example.flavorgo.data.model.CategoryResponse

data class FoodResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val foodCategory: CategoryResponse?,  // It's an object in JSON
    val images: List<String>?,
    val available: Boolean,
    val vegetarian: Boolean,
    val seasonal: Boolean
)
