package com.muhasib.snooq.mvvm.ViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhasib.snooq.mvvm.Repository.ProductListingRepository

class ProductListingViewModelFactory(private val repository: ProductListingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductListingViewModel(repository) as T
    }
}
