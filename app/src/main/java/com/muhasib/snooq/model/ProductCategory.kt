package com.muhasib.snooq.model

data class ProductCategory(

    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val stockQuantity: Int
)
