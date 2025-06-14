package com.example.flavorgo.data.model

data class Customer(
    val id: Long,
    val fullName: String,
    val email: String,
    val addresses: List<DeliveryAddress>
)