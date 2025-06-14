package com.example.flavorgo.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flavorgo.R
import com.example.flavorgo.data.model.Restaurant
import com.example.flavorgo.databinding.ItemRestaurantBinding

class RestaurantAdapter(
    private var restaurants: List<Restaurant>,
    private val onRestaurantClickListener: OnRestaurantClickListener? = null
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    interface OnRestaurantClickListener {
        fun onRestaurantClick(restaurantId: Long)
    }

    inner class RestaurantViewHolder(
        private val binding: ItemRestaurantBinding,
        private val listener: OnRestaurantClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(restaurant: Restaurant) {
            with(binding) {
                restaurantName.text = restaurant.name ?: "Unknown Restaurant"
                restaurantDescription.text = restaurant.description ?: "No description available"

                cuisineTypes.text = restaurant.cuisineType ?: "Cuisine not specified"

                // Improved address handling
                restaurantAddress.text = when {
                    restaurant.address?.toString().isNullOrEmpty() -> "Address not available"
                    else -> restaurant.address.toString()
                }

                // Improved opening hours handling
                openingHours.text = when {
                    restaurant.openingHours.isNullOrEmpty() -> "Hours not available"
                    else -> restaurant.openingHours
                }

                // Improved image loading with null safety
                val imageUrl = restaurant.images?.firstOrNull()
                Glide.with(root.context)
                    .load(if (!imageUrl.isNullOrEmpty()) imageUrl else null)
                    .placeholder(R.drawable.restaurant_placeholder)
                    .error(R.drawable.restaurant_placeholder)
                    .into(restaurantImage)

                restaurantCardView.setOnClickListener {
                    listener?.onRestaurantClick(restaurant.id)
                    Log.d("RestaurantAdapter", "Clicked restaurant ID: ${restaurant.id}") // Add this for debugging
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding, onRestaurantClickListener)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurants[position])
    }

    override fun getItemCount(): Int = restaurants.size

    fun updateData(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }
}