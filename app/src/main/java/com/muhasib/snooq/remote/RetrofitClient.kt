package com.muhasib.snooq.remote

import com.muhasib.snooq.api.RemoveBgApi
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Define your base URL without the API key
    private const val BASE_URL = "https://api.remove.bg/v1.0/" // Replace with the correct base URL of the API

    // Create an OkHttpClient with the interceptor to add the API key to every request
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("X-Api-Key", "whzYwU34qUJbk9mg7b7Cc4mh") // Add your API key here in the header
            .build()
        chain.proceed(newRequest)
    }.build()

    // Create and provide the Retrofit instance with the client and API service
    val instance: RemoveBgApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Use the base URL without the API key
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Use the client with API key in the header
            .build()
            .create(RemoveBgApi::class.java)
    }
}
