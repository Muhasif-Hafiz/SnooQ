package com.muhasib.snooq.mvvm.ApiService

import retrofit2.Call
import com.muhasib.snooq.model.NominatimResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {

    @GET("search")
    fun getLocation(
        @Query("q") address: String,
        @Query("format") format: String = "json"
    ): Call<List<NominatimResponse>>


}