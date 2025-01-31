package com.muhasib.snooq.model

import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("display_name") val displayName: String
)