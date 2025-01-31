package com.muhasib.snooq.model

data class CustomerReview(
    val userId: String,
    val rating: Int,
    val comment: String,
    val date: String
)
