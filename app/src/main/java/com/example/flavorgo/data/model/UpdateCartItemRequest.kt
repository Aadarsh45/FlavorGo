package com.example.flavorgo.data.model

data class UpdateCartItemRequest(
    val cartItemId: Long,
    val quantity: Int
)