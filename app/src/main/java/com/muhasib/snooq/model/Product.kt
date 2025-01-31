package com.muhasib.snooq.model

data class Product(
    val productId: String,
    val name: String,
    val description: String,
    val price: Double,
    val stockQuantity: Int,
    val imageUrl: String? = null,
    val category: String
)
