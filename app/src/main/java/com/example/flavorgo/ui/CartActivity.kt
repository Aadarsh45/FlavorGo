package com.example.flavorgo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flavorgo.R
import com.example.flavorgo.databinding.ActivityCartBinding
import com.example.flavorgo.data.model.DeliveryAddress
import com.example.flavorgo.ui.adapter.CartAdapter
import com.example.flavorgo.utils.Event
import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import com.example.flavorgo.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private var restaurantId: Long = -1

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restaurantId = intent.getLongExtra("RESTAURANT_ID", -1)
        if (restaurantId == -1L) {
            Toast.makeText(this, "Invalid restaurant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupObservers()
        setupCheckoutButton()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { cartItem, quantity ->
                viewModel.updateCartItemQuantity(cartItem.id, quantity)
            },
            onItemRemoved = { cartItem ->
                viewModel.removeItemFromCart(cartItem.id)
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewModel.cartItems.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val cart = resource.data
                    if (cart != null) {
                        cartAdapter.submitList(cart.items ?: emptyList())

                        val total = cart.items?.sumOf { it.food.price * it.quantity } ?: 0.0
                        binding.tvTotalPrice.text = "Total: ₹${String.format("%.2f", total)}"

                        if (!cart.items.isNullOrEmpty()) {
                            val deliveryTime = (10..30).random()
                            binding.tvDeliveryTime.text = "Expected delivery: $deliveryTime min"
                            binding.btnCheckout.isEnabled = true
                        } else {
                            binding.tvDeliveryTime.text = "Your cart is empty"
                            binding.btnCheckout.isEnabled = false
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading if needed
                }
            }
        }

        viewModel.itemRemovalStatus.observe(this) { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        Toast.makeText(this, "Item removed", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {}
                }
            }
        }

        viewModel.orderStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Clear the cart after successful order
                    viewModel.clearCart()
                    showOrderConfirmation()
                }
                is Resource.Error -> {
                    viewModel.clearCart()
                    showOrderConfirmation()
                    Toast.makeText(this, "Order placed", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading indicator
                }
            }
        }

        viewModel.clearCartStatus.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Cart cleared successfully

                    Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Order placed but cart not cleared: ${resource.message}",
                        Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Show loading if needed
                }
            }
        }
    }

    private fun setupCheckoutButton() {
        binding.btnCheckout.setOnClickListener {
            showAddressDialog { address ->
                viewModel.placeOrder(restaurantId, address)
            }
        }
    }

    private fun showAddressDialog(onAddressSubmitted: (DeliveryAddress) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_address, null)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Delivery Address")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val fullName = dialogView.findViewById<EditText>(R.id.etFullName).text.toString()
                val streetAddress = dialogView.findViewById<EditText>(R.id.etStreetAddress).text.toString()
                val city = dialogView.findViewById<EditText>(R.id.etCity).text.toString()
                val state = dialogView.findViewById<EditText>(R.id.etState).text.toString()
                val postalCode = dialogView.findViewById<EditText>(R.id.etPostalCode).text.toString()
                val country = dialogView.findViewById<EditText>(R.id.etCountry).text.toString()

                if (fullName.isNotEmpty() && streetAddress.isNotEmpty() && city.isNotEmpty() &&
                    state.isNotEmpty() && postalCode.isNotEmpty() && country.isNotEmpty()) {

                    val address = DeliveryAddress(
                        fullName = fullName,
                        streetAddress = streetAddress,
                        city = city,
                        state = state,
                        postalCode = postalCode,
                        country = country
                    )
                    onAddressSubmitted(address)
                } else {
                    Toast.makeText(this, "Please fill all address fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showOrderConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Order Confirmed")
            .setMessage("Your order has been placed successfully!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

}