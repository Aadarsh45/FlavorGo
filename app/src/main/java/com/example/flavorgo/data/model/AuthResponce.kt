package com.example.flavorgo.data.model

data class AuthResponse(
    val message: String,
    val jwt: String,
    val role: String
)