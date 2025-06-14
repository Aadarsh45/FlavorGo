package com.example.flavorgo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorgo.data.model.*
import com.example.flavorgo.data.repository.CartRepository
import com.example.flavorgo.utils.Event
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _cartItems = MutableLiveData<Resource<Cart>>()
    val cartItems: LiveData<Resource<Cart>> get() = _cartItems

    private val _itemRemovalStatus = MutableLiveData<Event<Resource<Unit>>>()
    val itemRemovalStatus: LiveData<Event<Resource<Unit>>> get() = _itemRemovalStatus

    private val _orderStatus = MutableLiveData<Resource<Order>>()
    val orderStatus: LiveData<Resource<Order>> get() = _orderStatus

    init {
        fetchCart()
    }
    private val _clearCartStatus = MutableLiveData<Resource<Cart>>()
    val clearCartStatus: LiveData<Resource<Cart>> get() = _clearCartStatus

    fun clearCart() {
        viewModelScope.launch {
            _clearCartStatus.value = Resource.Loading()
            try {
                val token = tokenManager.getToken() ?: throw Exception("Please login to clear cart")
                val response = cartRepository.clearCart(token)
                _clearCartStatus.value = Resource.Success(response)
                fetchCart() // Refresh the cart after clearing
            } catch (e: Exception) {
                _clearCartStatus.value = Resource.Error(e.message ?: "Failed to clear cart")
            }
        }
    }

    fun fetchCart() {
        viewModelScope.launch {
            _cartItems.value = Resource.Loading()
            try {
                val token = tokenManager.getToken() ?: throw Exception("Please login to access cart")
                val response = cartRepository.getUserCart(token)
                _cartItems.value = Resource.Success(response)
            } catch (e: Exception) {
                _cartItems.value = Resource.Error(e.message ?: "Failed to fetch cart")
            }
        }
    }

    fun addItemToCart(food: Food) {
        viewModelScope.launch {
            try {
                val request = AddCartItemRequest(
                    foodId = food.id,
                    quantity = 1,
                    price = food.price
                )
                cartRepository.addItemToCart(request)
                fetchCart()
            } catch (e: Exception) {
                _cartItems.value = Resource.Error(e.message ?: "Failed to add item")
            }
        }
    }

    fun updateCartItemQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            try {
                val request = UpdateCartItemRequest(cartItemId, quantity)
                cartRepository.updateCartItemQuantity(request)
                fetchCart()
            } catch (e: Exception) {
                _cartItems.value = Resource.Error(e.message ?: "Failed to update quantity")
            }
        }
    }

    fun removeItemFromCart(cartItemId: Long) {
        viewModelScope.launch {
            _itemRemovalStatus.value = Event(Resource.Loading())
            try {
                cartRepository.removeItemFromCart(cartItemId)
                _itemRemovalStatus.value = Event(Resource.Success(Unit))
                fetchCart()
            } catch (e: Exception) {
                _itemRemovalStatus.value = Event(Resource.Error(e.message ?: "Failed to remove item"))
            }
        }
    }

    fun placeOrder(restaurantId: Long, deliveryAddress: DeliveryAddress) {
        viewModelScope.launch {
            _orderStatus.value = Resource.Loading()
            try {
                val token = tokenManager.getToken() ?: throw Exception("Please login to place order")
                val response = cartRepository.placeOrder(token, restaurantId, deliveryAddress)
                _orderStatus.value = Resource.Success(response)
            } catch (e: Exception) {
                _orderStatus.value = Resource.Error(e.message ?: "Failed to place order")
            }
        }
    }
}