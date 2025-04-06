package com.muhasib.snooq.singleton

import com.muhasib.snooq.mvvm.ApiService.NominatimService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    // Interceptor to add User-Agent header

    private val interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "SnooQ/1.0 (muhasibhafeez91@example.com)")
            .build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val instance: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimService::class.java)
    }
}