package com.example.flavorgo.data.model

data class CartItem(
    val id: Long,
    val food: Food,
    val quantity: Int,
    val totalPrice: Double
)
