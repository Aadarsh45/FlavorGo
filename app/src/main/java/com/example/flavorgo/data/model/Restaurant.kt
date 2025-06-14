package com.example.flavorgo.data.model

data class Restaurant(
    val id: Long,
    val name: String,
    val description: String,
    val cuisineType: String,
    val address: Address,  // ✅ Change to Address object
    val contactInformation: ContactInformation,  // ✅ Change to ContactInformation object
    val openingHours: String,
    val images: List<String>
)





