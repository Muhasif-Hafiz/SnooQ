package com.muhasib.snooq.model

data class Shop(
    val shopId: String,
    val shopName: String,
    val shopCategory: String,
    val description: String,
    val address: String,
    val location: LocationDetails,


    val currentVisitors: Int,
    val visitCount: Int,
    val averageRating: Double,
    val ratingCount: Int,
    val offers: List<Offer>,
    val products: List<Product>,
    val qrCode: String,
    val customerReviews: List<CustomerReview>,
    val isVerified: Boolean,
    val verificationDocuments: List<String>,
    val subscriptionPlan: String,
    val subscriptionExpiry: String,
    val tags: List<String>,
    val shopImages: List<String>
)
