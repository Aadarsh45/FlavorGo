package com.example.flavorgo.data.model

data class NutritionalInfoObject(
    val calories: Double,
    val fat: NutrientInfo,
    val saturatedFat: NutrientInfo,
    val carbs: NutrientInfo,
    val sugar: NutrientInfo,
    val fiber: NutrientInfo,
    val protein: NutrientInfo,
    val sodium: NutrientInfo,
    val dailyIntakeReference: DailyIntakeReference? = null
)
