package com.example.flavorgo.data.repository


import android.util.Log
import com.example.flavorgo.data.model.OrderHistoryResponse
import com.example.flavorgo.network.ApiService
import com.example.flavorgo.utils.TokenManager
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.HttpException
import com.example.flavorgo.utils.Resource

interface OrderRepository {
    suspend fun getUserOrders(token: String): Resource<List<OrderHistoryResponse>>
}

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : OrderRepository {

    override suspend fun getUserOrders(token: String): Resource<List<OrderHistoryResponse>> {
        return try {
            Log.d("API_DEBUG", "Fetching orders with token: ${token.take(10)}...")
            val response = apiService.getUserOrders("Bearer $token")
            Log.d("API_DEBUG", "Orders received: ${response.size} items")
            if (response.isEmpty()) {
                Log.w("API_DEBUG", "Received empty orders list")
            }
            Resource.Success(response)
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed to fetch orders", e)
            Resource.Error("Failed to load orders: ${e.message}")
        }
    }
}