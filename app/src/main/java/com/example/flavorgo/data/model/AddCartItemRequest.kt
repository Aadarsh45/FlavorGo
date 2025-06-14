package com.example.flavorgo.data.model

import com.google.gson.annotations.SerializedName

data class AddCartItemRequest(  @SerializedName("menuItemId")  // Match the API expected field name
                                val foodId: Long,
                                val quantity: Int,
                                val price: Double? = null  // Optional if your API doesn't require it
                              )
