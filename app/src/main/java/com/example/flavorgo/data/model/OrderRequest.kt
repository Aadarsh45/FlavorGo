package com.example.flavorgo.data.model

data class OrderRequest(
    val restaurantId: Long,
    val deliveryAddress: DeliveryAddress
)
