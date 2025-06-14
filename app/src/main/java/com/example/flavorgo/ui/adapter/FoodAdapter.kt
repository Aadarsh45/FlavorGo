package com.example.flavorgo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flavorgo.R
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.databinding.ItemFoodBinding

class FoodAdapter(
    private var foods: List<Food>,
    private val onFoodClickListener: OnFoodClickListener? = null
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    interface OnFoodClickListener {
        fun onFoodClick(food: Food)
    }

    inner class FoodViewHolder(
        private val binding: ItemFoodBinding,
        private val listener: OnFoodClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            // Set food name
            binding.foodName.text = food.name

            // Set food price
            binding.foodPrice.text = String.format("$%.2f", food.price)

            // Set food description
            binding.foodDescription.text = food.description

            // Set vegetarian status
            binding.vegetarianBadge.apply {
                text = if (food.isVegetarian) "Vegetarian" else "Non-Vegetarian"
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (food.isVegetarian) R.color.vegetarian_green else R.color.non_vegetarian_red
                    )
                )
            }

            // Set availability
            binding.availabilityBadge.apply {
                text = if (food.isAvailable) "Available" else "Sold Out"
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (food.isAvailable) R.color.available_green else R.color.sold_out_red
                    )
                )
            }

            // Load food image
            Glide.with(binding.root.context)
                .load(food.images?.firstOrNull())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.foodImage)

            // Set click listener
            binding.root.setOnClickListener {
                listener?.onFoodClick(food)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding, onFoodClickListener)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size
    fun updateData(newFoods: List<Food>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}