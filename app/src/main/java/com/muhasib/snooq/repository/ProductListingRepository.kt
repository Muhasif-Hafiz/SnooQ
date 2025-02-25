package com.muhasib.snooq.repository

import com.muhasib.snooq.api.RemoveBgApi
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class ProductListingRepository(private val apiService: RemoveBgApi) {

    suspend fun removeBackground(part: MultipartBody.Part): Response<ResponseBody> {
        return apiService.removeBg(part)
    }
}