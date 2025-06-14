package com.flavorgo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flavorgo.data.model.Address
import com.example.flavorgo.data.model.Category
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.data.model.Restaurant
import com.example.flavorgo.databinding.FragmentHomeBinding
import com.example.flavorgo.ui.RestaurantActivity
import com.example.flavorgo.ui.adapters.CategoryAdapter
import com.example.flavorgo.ui.adapters.FoodAdapter
import com.example.flavorgo.ui.adapters.RestaurantAdapter
import com.example.flavorgo.ui.fragments.ProfileFragment
import com.flavorgo.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(),RestaurantAdapter.OnRestaurantClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews() // <--- CORRECT: setupRecyclerViews() is called FIRST
        loadDummyData() // <--- Now this is called AFTER adapters are initialized
        observeViewModel()
        fetchAllData()
        binding.profileImage.setOnClickListener{
            startActivity(Intent(requireContext(), ProfileFragment::class.java))

        }
    }

    private fun loadDummyData() {


        val dummyCategories = listOf(
            Category(3, "Burgers", "https://source.unsplash.com/200x200/?burger"),
            Category(2, "Desserts", "https://source.unsplash.com/200x200/?dessert"),
            Category(1, "Drinks", "https://source.unsplash.com/200x200/?drink")
        )

        val dummyFoods = listOf(
            Food(1, "Pizza Margherita", "A classic Italian pizza with tomato sauce, mozzarella cheese, and basil.", 12.99, "Pizza", listOf("https://source.unsplash.com/200x200/?pizza"), true, true),
            Food(2, "Chicken Tikka Masala", "Creamy tomato-based curry with tender chicken pieces.", 15.99, "Indian", listOf("https://source.unsplash.com/200x200/?chicken-tikka-masala"), false, true),
            Food(3, "Sushi Combo", "Assorted sushi rolls and nigiri.", 18.50, "Japanese", listOf("https://source.unsplash.com/200x200/?sushi"), false, true)
        )


        restaurantAdapter.notifyDataSetChanged()
        categoryAdapter.updateData(dummyCategories)
        categoryAdapter.notifyDataSetChanged()
        foodAdapter.updateData(dummyFoods)
        foodAdapter.notifyDataSetChanged()
    }
    private fun setupRecyclerViews() {
        // Initialize with empty list AND the click listener
        restaurantAdapter = RestaurantAdapter(emptyList(), this)
        categoryAdapter = CategoryAdapter(emptyList())
        foodAdapter = FoodAdapter(emptyList())

        binding.restaurantsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = restaurantAdapter
        }

        binding.categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        binding.foodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = foodAdapter
        }
    }


    private fun observeViewModel() {
        homeViewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            Log.d("Restaurant_HomeFragment", "Restaurants received: ${restaurants.size}")

            restaurants.forEach { restaurant ->
                Log.d("Restaurant_HomeFragment", "Restaurant: ID=${restaurant.id}, Name=${restaurant.name}, Cuisine=${restaurant.cuisineType}")
            }

            // Don't create a new adapter - just update the existing one
            restaurantAdapter.updateData(restaurants)
        }

        homeViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateData(categories)
        }

        homeViewModel.foods.observe(viewLifecycleOwner) { foodItems ->
            foodAdapter.updateData(foodItems)
        }
    }


    private fun fetchAllData() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.fetchData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRestaurantClick(restaurantId: Long) {
        Log.d("HomeFragment", "Restaurant clicked with ID: $restaurantId")
        val intent = Intent(requireContext(), RestaurantActivity::class.java).apply {
            putExtra("RESTAURANT_ID", restaurantId)
        }
        startActivity(intent)
    }
}