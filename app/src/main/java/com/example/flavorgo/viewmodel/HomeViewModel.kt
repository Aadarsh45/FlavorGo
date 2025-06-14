package com.flavorgo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flavorgo.data.model.Category
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.data.model.Restaurant
import com.example.flavorgo.data.repository.CategoryRepository
import com.example.flavorgo.data.repository.FoodRepository
import com.example.flavorgo.data.repository.RestaurantRepository
import com.example.flavorgo.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val categoryRepository: CategoryRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> get() = _restaurants

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _foods = MutableLiveData<List<Food>>()
    val foods: LiveData<List<Food>> get() = _foods

    fun fetchData() {
        viewModelScope.launch {

            val categoryResponse = categoryRepository.getCategories()
            val foodResponse = foodRepository.getFoods()

                    val restaurantResponse = restaurantRepository.getRestaurants()

                    if (restaurantResponse is Resource.Success) {
                        _restaurants.postValue(restaurantResponse.data ?: emptyList())

                        restaurantResponse.data?.forEach { restaurant ->
                            Log.d("Restaurant_HomeViewModel", "Fetched Restaurant: ID=${restaurant.id}, Name=${restaurant.name}, Cuisine=${restaurant.cuisineType}, Address=${restaurant.address.city}")
                        }
                    } else {
                        Log.e("Restaurant_HomeViewModel", "Failed to fetch restaurants: ${restaurantResponse.message}")
                    }





            if (categoryResponse is Resource.Success) {
                _categories.postValue(categoryResponse.data ?: emptyList())
            }

            if (foodResponse is Resource.Success) {
                _foods.postValue(foodResponse.data ?: emptyList())
            }
        }
    }
}
