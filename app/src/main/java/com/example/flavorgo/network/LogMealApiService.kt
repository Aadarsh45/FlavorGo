package com.example.flavorgo.network

import com.example.flavorgo.data.model.NutritionalRequest
import com.example.flavorgo.data.model.NutritionalResponse
import com.example.flavorgo.data.model.RecognitionResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LogMealApiService {
//    @Multipart
//    @POST("image/segmentation/complete")
//    fun recognizeFood(
//        @Header("Authorization") auth: String,
//        @Part image: MultipartBody.Part
//    ): Call<RecognitionResponse>
//
//    @POST("nutrition/recipe/nutritionalInfo")
//    fun getNutritionalInfo(
//        @Header("Authorization") auth: String,
//        @Body request: NutritionalRequest
//    ): Call<NutritionalResponse>

    @Multipart
    @POST("image/segmentation/complete/{model_version}")
    fun recognizeFood(
        @Header("Authorization") auth: String,
        @Part image: MultipartBody.Part,
        @Path("model_version") modelVersion: String = "v1.0",
        @Query("language") language: String = "eng"
    ): Call<RecognitionResponse>

    @POST("nutrition/recipe/nutritionalInfo/{model_version}")
    fun getNutritionalInfo(
        @Header("Authorization") auth: String,
        @Body request: NutritionalRequest,
        @Path("model_version") modelVersion: String = "v1.0",
        @Query("language") language: String = "eng"
    ): Call<NutritionalResponse>

    // Optional: Endpoint to confirm dish (recommended before getting nutritional info)
    @POST("image/confirm/dish/{model_version}")
    fun confirmDish(
        @Header("Authorization") auth: String,
        @Body confirmRequest: Map<String, Any>,
        @Path("model_version") modelVersion: String = "v1.0",
        @Query("language") language: String = "eng"
    ): Call<Map<String, Any>>
}