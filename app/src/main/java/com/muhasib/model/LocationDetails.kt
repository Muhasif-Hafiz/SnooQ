package com.muhasib.model

data class LocationDetails(

    val fullAddress : String? = null,
    val openingHour: Int,
    val openingMinute: Int,
    val closingHour: Int,
    val closingMinute: Int,
    val selectedDays: String? = null ,
    val DeliveryAvailable : Boolean? = null,
    val DeliveryRadius : String? = null

)
