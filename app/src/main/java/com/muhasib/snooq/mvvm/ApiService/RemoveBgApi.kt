package com.muhasib.snooq.mvvm.ApiService

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RemoveBgApi {
    @Multipart
    @POST("removebg")
    suspend fun removeBg(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>
}
