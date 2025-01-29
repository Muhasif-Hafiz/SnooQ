package com.muhasib.model

data class LocationDetails(

    val fullAddress : String? = null,
    val openingTime: String,
    val closingTime: String,

    val selectedDays: String? = null ,
    val DeliveryAvailable : Boolean? = null,
    val DeliveryRadius : String? = null

)
