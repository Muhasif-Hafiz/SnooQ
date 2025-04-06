package com.muhasib.snooq.mvvm.ViewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhasib.snooq.mvvm.Repository.ProductListingRepository
import com.muhasib.snooq.mvvm.Repository.UploadProductRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class ProductListingViewModel(private val repository: ProductListingRepository) : ViewModel() {

    val AppWriteRepository = UploadProductRepository()

    suspend fun uploadToAppWrite(context: Context, file: File) {
        return AppWriteRepository.uploadImageToAppWrite(context, file = file)
    }
}