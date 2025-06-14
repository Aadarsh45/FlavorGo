package com.example.flavorgo.data.model

data class OrderHistoryResponse(
    val id: Long,
    val customer: Customer,
    val totalAmount: Double,
    val orderStatus: String,
    val createdAt: String,
    val deliveryAddress: DeliveryAddress,
    val items: List<OrderItem>,
//    val payment: Payment?,
    val totalItem: Int,
    val totalPrice: Double
)
