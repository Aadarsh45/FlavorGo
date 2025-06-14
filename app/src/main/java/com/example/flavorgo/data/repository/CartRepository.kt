package com.example.flavorgo.data.repository

import android.util.Log
import com.example.flavorgo.data.model.AddCartItemRequest
import com.example.flavorgo.data.model.Cart
import com.example.flavorgo.data.model.CartItem
import com.example.flavorgo.data.model.DeliveryAddress
import com.example.flavorgo.data.model.Order
import com.example.flavorgo.data.model.OrderRequest
import com.example.flavorgo.data.model.UpdateCartItemRequest
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.TokenManager
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

interface CartRepository {
    suspend fun clearCart(token: String): Cart
    suspend fun getUserCart(token: String): Cart
    suspend fun addItemToCart(request: AddCartItemRequest): CartItem
    suspend fun updateCartItemQuantity(request: UpdateCartItemRequest): CartItem
    suspend fun removeItemFromCart(cartItemId: Long): Cart
    suspend fun placeOrder(token: String, restaurantId: Long, deliveryAddress: DeliveryAddress): Order
}

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : CartRepository {

    override suspend fun clearCart(token: String): Cart {
        return apiService.clearCart("Bearer $token")
    }

    override suspend fun getUserCart(token: String): Cart {
        Log.d("API_DEBUG", "===== getUserCart =====")
        Log.d("API_DEBUG", "Endpoint: /cart/")
        Log.d("API_DEBUG", "Token: Bearer ${token.take(10)}...")

        return try {
            val response = apiService.findUserCart("Bearer $token")
            Log.d("API_DEBUG", "Response: $response")
            response
        } catch (e: Exception) {
            Log.e("API_DEBUG", "Error in getUserCart: ${e.message}")
            throw e
        }
    }

    override suspend fun addItemToCart(request: AddCartItemRequest): CartItem {
        val token = tokenManager.getToken() ?: throw Exception("Not authenticated")

        Log.d("API_DEBUG", "Making add to cart request: $request")

        return try {
            val response = apiService.addItemToCart(request, "Bearer $token")
            Log.d("API_DEBUG", "Add to cart success: $response")
            response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("API_ERROR", """
            Add to cart failed:
            Code: ${e.code()}
            Message: ${e.message}
            Error Body: $errorBody
        """.trimIndent())
            throw Exception(errorBody ?: "Failed to add item to cart")
        }
    }

    override suspend fun updateCartItemQuantity(request: UpdateCartItemRequest): CartItem {
        val token = tokenManager.getToken() ?: throw Exception("Not authenticated")
        return apiService.updateCartItemQuantity(request, "Bearer $token")
    }

    override suspend fun removeItemFromCart(cartItemId: Long): Cart {
        val token = tokenManager.getToken() ?: throw Exception("Not authenticated")
        return apiService.removeItemFromCart(cartItemId, "Bearer $token")
    }

    override suspend fun placeOrder(token: String, restaurantId: Long, deliveryAddress: DeliveryAddress): Order {
        val request = OrderRequest(
            restaurantId = restaurantId,
            deliveryAddress = deliveryAddress
        )
        return apiService.placeOrder("Bearer $token", request)
    }
}