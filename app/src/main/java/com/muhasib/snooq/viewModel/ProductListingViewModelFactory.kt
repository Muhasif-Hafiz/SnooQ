package com.muhasib.snooq.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muhasib.snooq.repository.ProductListingRepository

class ProductListingViewModelFactory(private val repository: ProductListingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductListingViewModel(repository) as T
    }
}
