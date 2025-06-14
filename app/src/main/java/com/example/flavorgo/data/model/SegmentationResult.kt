package com.example.flavorgo.data.model

data class SegmentationResult(
    val food_item_position: Int,
    val bounding_box: BoundingBox,
    val recognition_results: List<RecognitionResult>? = null
)
