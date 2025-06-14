package com.example.flavorgo.data.model

data class OrderItem(
    val id: Long,
    val food: Food,
    val quantity: Int,
    val totalPrice: Double
)