package com.example.flavorgo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flavorgo.R
import com.example.flavorgo.data.model.CartItem
import com.example.flavorgo.data.model.Food
import com.example.flavorgo.databinding.ItemResFoodBinding

class FoodItemAdapter(
    private var foodList: List<Food>,
    private val onAddToCart: (Food) -> Unit,
    private val onQuantityChanged: (Food, Int) -> Unit,
    private val cartItems: List<CartItem> = emptyList()
) : RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder>() {

    private val itemQuantities = mutableMapOf<Long, Int>()

    inner class FoodViewHolder(val binding: ItemResFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.apply {
                // Set food details
                val cartItem = cartItems.find { it.food.id == food.id }
                val initialQuantity = cartItem?.quantity ?: 0

                // Initialize quantity from cart if exists
                itemQuantities[food.id] = initialQuantity
                updateQuantityViews(initialQuantity)

                tvFoodName.text = food.name
                tvFoodDesc.text = food.description
                tvFoodPrice.text = "₹${food.price}"
                tvAvailability.text = if (food.isAvailable) "Available" else "Not Available"
                tvAvailability.setTextColor(
                    if (food.isAvailable) root.context.getColor(R.color.available_green)
                    else root.context.getColor(R.color.not_available_red)
                )

                tvFoodCategory.text = food.category ?: "No category"
                tvFoodCategory.visibility = if (food.category.isNullOrEmpty()) View.GONE else View.VISIBLE

                ivVegIcon.setImageResource(
                    if (food.isVegetarian) R.drawable.ic_veg else R.drawable.ic_non_veg
                )

                // Load food image
                if (!food.images.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load(food.images.first())
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(ivFoodImage)
                } else {
                    ivFoodImage.setImageResource(R.drawable.ic_placeholder)
                }

                // Handle cart operations
                val quantity = itemQuantities[food.id] ?: 0

                btnAddToCart.setOnClickListener {
                    if (quantity == 0) {
                        onAddToCart(food)
                        itemQuantities[food.id] = 1
                        updateQuantityViews(1)
                    }
                }

                btnIncrease.setOnClickListener {
                    val newQuantity = quantity + 1
                    itemQuantities[food.id] = newQuantity
                    updateQuantityViews(newQuantity)
                    onQuantityChanged(food, newQuantity)
                }

                btnDecrease.setOnClickListener {
                    val newQuantity = (quantity - 1).coerceAtLeast(0)
                    itemQuantities[food.id] = newQuantity
                    updateQuantityViews(newQuantity)
                    onQuantityChanged(food, newQuantity)
                }

                updateQuantityViews(quantity)
            }
        }

        private fun updateQuantityViews(quantity: Int) {
            binding.apply {
                if (quantity > 0) {
                    btnAddToCart.visibility = View.GONE
                    btnDecrease.visibility = View.VISIBLE

                    btnIncrease.visibility = View.VISIBLE

                } else {
                    btnAddToCart.visibility = View.VISIBLE
                    btnDecrease.visibility = View.GONE

                    btnIncrease.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemResFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.bind(food)
    }

    override fun getItemCount(): Int = foodList.size

    fun updateList(newList: List<Food>) {
        foodList = newList
        // Preserve quantities when updating list
        val newQuantities = mutableMapOf<Long, Int>()
        newList.forEach { food ->
            newQuantities[food.id] = itemQuantities[food.id] ?: 0
        }
        itemQuantities.clear()
        itemQuantities.putAll(newQuantities)
        notifyDataSetChanged()
    }

    fun setInitialQuantities(quantities: Map<Long, Int>) {
        itemQuantities.clear()
        itemQuantities.putAll(quantities)
        notifyDataSetChanged()
    }
}