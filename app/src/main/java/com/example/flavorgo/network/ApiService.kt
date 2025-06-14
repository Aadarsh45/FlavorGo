package com.example.flavorgo.network

import com.example.flavorgo.data.model.*
import com.example.flavorgo.data.model.Category
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.data.model.Restaurant
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/signup")
    suspend fun signUp(@Body user: User): AuthResponse

    @POST("auth/signin")
    suspend fun signIn(@Body credentials: LoginRequest): AuthResponse

    @GET("categories")
    suspend fun getCategories(@Header("Authorization") token: String): List<Category>

    @GET("api/restaurants")
    suspend fun getRestaurants(@Header("Authorization") token: String): List<Restaurant>

    @GET("foods")
    suspend fun getFoods(@Header("Authorization") token: String): List<Food>

//    @GET("api/food/restaurant/{restaurantId}")
//    suspend fun getRestaurantFoods(
//        @Path("restaurantId") restaurantId: Long,
//        @Header("Authorization") token: String,
//
//
//
//    ): List<FoodResponse>

//    @GET("api/food/restaurant/{restaurantId}")
//    suspend fun getRestaurantFoods(
//        @Path("restaurantId") restaurantId: Long,
//
//        @Header("Authorization") token: String
//    ): List<FoodResponse>

    @GET("api/food/restaurant/{restaurantId}")
    suspend fun getRestaurantFoods(
        @Path("restaurantId") restaurantId: Long,
        @Header("Authorization") token: String,
//        @Query("vegetarian") vegetarian: Boolean? = null
    ): List<FoodResponse>

    //cart
    @GET("/api/cart/")
    suspend fun findUserCart(
        @Header("Authorization") jwt: String
    ): Cart

    @PUT("/api/cart/add")
    suspend fun addItemToCart(
        @Body request: AddCartItemRequest,
        @Header("Authorization") jwt: String
    ): CartItem

    @PUT("/api/cart-item/update")
    suspend fun updateCartItemQuantity(
        @Body request: UpdateCartItemRequest,
        @Header("Authorization") jwt: String
    ): CartItem

    @DELETE("/api/cart-item/{id}/remove")
    suspend fun removeItemFromCart(
        @Path("id") cartItemId: Long,
        @Header("Authorization") jwt: String
    ): Cart

    @POST("/api/cart/checkout")
    suspend fun placeOrder(
        @Header("Authorization") jwt: String
    ): Order



    @POST("/api/order")
    suspend fun placeOrder(
        @Header("Authorization") jwt: String,
        @Body request: OrderRequest
    ): Order

    @PUT("/api/cart/clear")
    suspend fun clearCart(
        @Header("Authorization") jwt: String
    ): Cart
    @GET("api/order/user")
    suspend fun getUserOrders(
        @Header("Authorization") jwt: String
    ): List<OrderHistoryResponse>

    @GET("api/users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

}
