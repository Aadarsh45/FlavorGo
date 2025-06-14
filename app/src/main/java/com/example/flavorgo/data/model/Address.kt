package com.example.flavorgo.data.model


data class Address(
    val street: String? = null,
    val city: String? = null,
    val country: String? = null,
    val state: String? = null,
    val zipCode: String? = null
) {
    fun getFullAddress(): String {
        val parts = mutableListOf<String>()
        street?.let { parts.add(it) }
        city?.let { parts.add(it) }
        state?.let { parts.add(it) }
        zipCode?.let { parts.add(it) }
        return parts.joinToString(", ")
    }

    override fun toString(): String {
        return getFullAddress()
    }
}