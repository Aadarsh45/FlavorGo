package com.example.flavorgo.data.model

import java.io.Serializable

data class NutritionalResponse(
    val imageId: Int,
    val foodName: List<String>,
    val ids: Any, // This can be an Int or List<Int> based on API docs
    val hasNutritionalInfo: Boolean,
    val serving_size: Double? = null,
    val nutritional_info: NutritionalInfoObject? = null,
    val nutritional_info_per_item: List<NutritionalInfoPerItem>? = null
)
