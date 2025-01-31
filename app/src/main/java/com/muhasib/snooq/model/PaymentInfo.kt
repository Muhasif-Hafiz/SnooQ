package com.muhasib.snooq.model

data class PaymentInfo(
    val paymentMethod : String? = null,
    val bankName : String? = null,
    val accountNumber : String? = null,
    val ifscCode : String? = null,
    val refundPolicy : String? = null
)
