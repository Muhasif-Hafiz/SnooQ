package com.muhasib.snooq.mvvm.ViewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhasib.snooq.mvvm.Repository.ProductListingRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProductListingViewModel(private val repository: ProductListingRepository) : ViewModel() {

    private val _processedImage = MutableLiveData<Bitmap>()
    val processedImage: LiveData<Bitmap> get() = _processedImage

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun removeBackground(part: MultipartBody.Part) {
        _loading.value = true

        viewModelScope.launch {
            try {
                val response = repository.removeBackground(part)

                if (response.isSuccessful) {
                    val inputStream = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    _processedImage.value = bitmap
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}