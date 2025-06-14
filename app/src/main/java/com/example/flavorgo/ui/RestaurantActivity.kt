
package com.example.flavorgo.ui


import android.content.Intent

import android.os.Bundle


import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flavorgo.R

import com.example.flavorgo.databinding.ActivityRestaurantBinding
import com.example.flavorgo.ui.adapter.FoodItemAdapter
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.viewmodel.CartViewModel
import com.example.flavorgo.viewmodel.RestaurantViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantBinding
    private val restaurantViewModel: RestaurantViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var foodAdapter: FoodItemAdapter

    private var restaurantId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restaurantId = intent.getLongExtra("RESTAURANT_ID", -1)
        if (restaurantId == -1L) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupChips()
        setupRecyclerView()
        setupObservers()
        setupCartFAB()
        fetchInitialFoodItems()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.btnBack.setOnClickListener { finish() }
        binding.btnFavorite.setOnClickListener { addToFav() }
        binding.btnShare.setOnClickListener { share() }
    }

    private fun share() {
        Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show()
    }

    private fun addToFav() {
        Toast.makeText(this, "Marked as Favorite", Toast.LENGTH_SHORT).show()
    }

    private fun setupChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, _ ->
            restaurantViewModel.fetchRestaurantFoods(
                restaurantId = restaurantId,
                vegetarian = true
            )
        }
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodItemAdapter(
            emptyList(),
            onAddToCart = { food ->
                cartViewModel.addItemToCart(food)
                Toast.makeText(this, "${food.name} added to cart", Toast.LENGTH_SHORT).show()
            },
            onQuantityChanged = { food, quantity ->
                // In a real implementation, you would need to map food ID to cart item ID
                // For simplicity, we'll assume the food ID is the same as cart item ID
                cartViewModel.updateCartItemQuantity(food.id, quantity)
            }
        )

        binding.rvResFood.apply {
            layoutManager = LinearLayoutManager(this@RestaurantActivity)
            adapter = foodAdapter
        }
    }



    private fun setupCartFAB() {
        val fab = findViewById<FloatingActionButton>(R.id.fabCart)
        fab.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java).apply {
                putExtra("RESTAURANT_ID", restaurantId)
            }
            startActivity(intent)
        }

        // Show cart item count badge if needed
        cartViewModel.cartItems.observe(this) { resource ->
            if (resource is Resource.Success) {
                val itemCount = resource.data?.items?.sumOf { it.quantity } ?: 0
                if (itemCount > 0) {
                    // You can add a badge to the FAB here
                }
            }
        }
    }

    private fun setupObservers() {
        restaurantViewModel.foodItems.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { foods ->
                        foodAdapter.updateList(foods)

                        // Load initial quantities from cart
                        cartViewModel.cartItems.value?.let { cartResource ->
                            if (cartResource is Resource.Success) {
                                val quantities = cartResource.data?.items?.associate {
                                    it.food.id to it.quantity
                                } ?: emptyMap()
                                foodAdapter.setInitialQuantities(quantities)
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    // Show loading indicator if needed
                }
            }
        }

        cartViewModel.cartItems.observe(this) { resource ->
            // Handle cart updates if needed
        }
    }

    private fun fetchInitialFoodItems() {
        restaurantViewModel.fetchRestaurantFoods(restaurantId)
        cartViewModel.fetchCart() // Load user's cart
    }

    private fun showOrderConfirmation() {
        Toast.makeText(this, "Order confirmed", Toast.LENGTH_SHORT).show()
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_order_confirmation)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setCancelable(false)
//
//        val lottieAnimation = dialog.findViewById<LottieAnimationView>(R.id.lottieAnimation)
//        lottieAnimation.playAnimation()
//
//        dialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
//            dialog.dismiss()
//            finish()
    }

//        dialog.show()


}
