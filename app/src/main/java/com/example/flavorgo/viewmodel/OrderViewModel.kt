package com.example.flavorgo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorgo.data.model.OrderHistoryResponse
import com.example.flavorgo.data.repository.OrderRepository
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _orders = MutableLiveData<Resource<List<OrderHistoryResponse>>>()
    val orders: LiveData<Resource<List<OrderHistoryResponse>>> = _orders

    fun fetchUserOrders() {
        viewModelScope.launch {
            _orders.value = Resource.Loading()
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    _orders.value = Resource.Error("Not logged in")
                    return@launch
                }
                _orders.value = orderRepository.getUserOrders(token)
            } catch (e: Exception) {
                _orders.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}