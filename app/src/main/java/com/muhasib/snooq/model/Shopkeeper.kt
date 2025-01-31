package com.muhasib.snooq.model

data class Shopkeeper(
    val shopkeeperId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val profilePicture: String? = null,
    val registrationDate: String,
     val paymentInfo: PaymentInfo,
    val shops: List<Shop>
)
typealias ShopkeepersCollection = Map<String, Shopkeeper>