package com.example.flavorgo.ui


import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import com.example.flavorgo.databinding.ActivityOrderHistoryBinding
import com.example.flavorgo.viewmodel.OrderViewModel
import com.example.flavorgo.ui.adapter.OrderHistoryAdapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flavorgo.R

import com.example.flavorgo.utils.Resource
import com.example.flavorgo.utils.TokenManager
import com.example.flavorgo.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private val viewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        viewModel.fetchUserOrders()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = OrderHistoryAdapter()
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    // In OrderHistoryActivity.kt
    private fun setupObservers() {
        viewModel.orders.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (resource.data.isNullOrEmpty()) {
                        showEmptyState()  // Show empty state UI
                        Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show()
                    } else {
                        (binding.rvOrders.adapter as OrderHistoryAdapter).submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.apply {
            rvOrders.visibility = View.GONE
            emptyStateView.visibility = View.VISIBLE
            emptyStateText.text = "You haven't placed any orders yet"
            emptyStateImage.setImageResource(R.drawable.ic_empty_orders)
        }
    }
}