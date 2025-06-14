package com.example.flavorgo.data.model

data class Order(
    val id: Long,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: String,
    val createdAt: String
)