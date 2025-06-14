package com.example.flavorgo.data.model

data class Food(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val category: String?,
    val images: List<String>?,
    val isVegetarian: Boolean,
    val isAvailable: Boolean
)
