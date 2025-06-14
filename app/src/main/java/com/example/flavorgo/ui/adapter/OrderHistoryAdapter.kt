package com.example.flavorgo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flavorgo.data.model.DeliveryAddress
import com.example.flavorgo.data.model.OrderHistoryResponse
import com.example.flavorgo.databinding.ItemOrderBinding

import java.text.SimpleDateFormat
import java.util.Locale

class qOrderHistoryAdapter : ListAdapter<OrderHistoryResponse, OrderHistoryAdapter.OrderViewHolder>(
    DiffCallback()
) {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderHistoryResponse) {
            binding.apply {
                tvOrderId.text = "Order #${order.id}"
                tvOrderDate.text = formatDate(order.createdAt)
                tvOrderStatus.text = order.orderStatus
                tvOrderAmount.text = "₹${order.totalPrice}"
                tvDeliveryAddress.text = formatAddress(order.deliveryAddress)

                // Setup items recycler view
                rvOrderItems.layoutManager = LinearLayoutManager(root.context)
                rvOrderItems.adapter = OrderItemAdapter().apply {
                    submitList(order.items)
                }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }

        private fun formatAddress(address: DeliveryAddress): String {
            return "${address.streetAddress}, ${address.city}, ${address.state} - ${address.postalCode}, ${address.country}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderHistoryResponse>() {
        override fun areItemsTheSame(oldItem: OrderHistoryResponse, newItem: OrderHistoryResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderHistoryResponse, newItem: OrderHistoryResponse): Boolean {
            return oldItem == newItem
        }
    }
}

