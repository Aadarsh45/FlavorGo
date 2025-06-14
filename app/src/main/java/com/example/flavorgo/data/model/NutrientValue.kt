package com.example.flavorgo.data.model

import java.io.Serializable

data class NutrientValue(
    val value: Float,
    val unit: String
) : Serializable
