package com.muhasib.snooq.mvvm.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.model.DownloadShopkeeper
import com.muhasib.snooq.mvvm.Repository.ShopRepository

class shopViewModel(application: Application) : AndroidViewModel(application) {


    private val repository = ShopRepository(application)
    val shopData: LiveData<DownloadShopkeeper>

    init {
        val shopId = MySharedPreferences(application).getShopId()
        Log.d("ShopViewModel", "shopId: $shopId")
        shopData = shopId?.let { repository.fetchShopData(it) } ?: MutableLiveData()
        Log.d("ShopViewModel", "shopData: $shopData")
    }

    fun getProfileImageUrlFromPrefs(): String? {
        return repository.getProfileImageUrlFromPrefs()
    }
    fun saveProfileImageUrlToPrefs(imageUrl: String) {
        repository.saveProfileImageUrlToPrefs(imageUrl)
    }
    fun getBannerImageUrlFromPrefs() : String? {
        return repository.getBannerImageUrlFromPrefs()
    }
    fun saveBannerImageUrlToPrefs( imageUrl : String){
        repository.saveBannerImageUrlToPrefs(imageUrl)
    }


}