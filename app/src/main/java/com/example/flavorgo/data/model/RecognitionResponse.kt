package com.example.flavorgo.data.model

import java.io.Serializable

data class RecognitionResponse(
    val imageId: Int,
    val foodType: FoodType? = null,
    val segmentation_results: List<SegmentationResult>? = null,
    val processed_image_size: ProcessedImageSize? = null
)
