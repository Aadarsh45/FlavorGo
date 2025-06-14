package com.example.flavorgo.data.model

data class Cart(
    val id: Long,
    val userId: Long,
    val items: List<CartItem>,
    val totalPrice: Double
)
