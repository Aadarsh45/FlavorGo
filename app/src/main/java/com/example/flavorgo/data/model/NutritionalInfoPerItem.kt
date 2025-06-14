package com.example.flavorgo.data.model

data class NutritionalInfoPerItem(
    val food_item_position: Int,
    val dish_id: Int,
    val dish_name: String,
    val nutritional_info: NutritionalInfoObject? = null,
    val serving_size: Double? = null
)
