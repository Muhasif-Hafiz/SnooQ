package com.muhasib.snooq.model

data class Product(
    val shopId: String,
    val productId: String,
    val name: String,
    val description: String,
    val price: Double? = null,
    val discountedPrice : Double? = null,
    val stockQuantity: Int? = null,
    val imageUrl1: String? = null,
    val imageUrl2 : String? = null,
    val imageUrl3 : String? = null,
    var imageUrl4 : String? = null,
    val category: String? = null,
    val subCategory : String? = null,
    val size : String? = null,
    val color : String? = null,
    val isDeliveryAvailable: Boolean? = null

)
