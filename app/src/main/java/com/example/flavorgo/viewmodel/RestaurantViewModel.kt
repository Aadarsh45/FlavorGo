package com.example.flavorgo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.data.repository.ResFoodRepository
import com.example.flavorgo.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val resFoodRepository: ResFoodRepository  // Inject ResFoodRepository directly
) : ViewModel() {

    private val _foodItems = MutableLiveData<Resource<List<Food>>>()
    val foodItems: LiveData<Resource<List<Food>>> get() = _foodItems

    fun fetchRestaurantFoods(restaurantId: Long, vegetarian: Boolean? = null) {
        viewModelScope.launch {
            _foodItems.value = Resource.Loading()
            try {
                val response = resFoodRepository.getRestaurantFoods(restaurantId)
                _foodItems.value = response
            } catch (e: Exception) {
                _foodItems.value = Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}