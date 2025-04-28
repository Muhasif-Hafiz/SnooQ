package com.muhasib.snooq.mvvm.Repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.model.DownloadShopkeeper

class ShopRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    private val sharedPreferencesBanner : SharedPreferences = context.getSharedPreferences("MyPrefsBanner", Context.MODE_PRIVATE)

    val prefs = MySharedPreferences(context)
    fun fetchShopData(shopId: String): LiveData<DownloadShopkeeper> {
        val shopLiveData = MutableLiveData<DownloadShopkeeper>()

        db.collection("shops").document(shopId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val shopkeeperData = document.data?.values?.firstOrNull() as? Map<*, *>
                    val shopkeeperName = shopkeeperData?.get("name") as? String ?: "N/A"
                    val shopkeeperEmail = shopkeeperData?.get("email") as? String ?: "N/A"
                    val shopsList = shopkeeperData?.get("shops") as? List<Map<*, *>>

                    if (!shopsList.isNullOrEmpty()) {
                        val shopData = shopsList[0]
                        val shopImages = (shopData["shopImages"] as? List<String>) ?: emptyList()
                        val shop = DownloadShopkeeper(
                            shopkeeperName = shopkeeperName,
                            shopkeeperEmail = shopkeeperEmail,
                            shopName = shopData["shopName"] as? String ?: "N/A",
                            shopDescription = shopData["description"] as? String ?: "N/A",
                            address = shopData["address"] as? String ?: "N/A",
                            openingTime = (shopData["location"] as? Map<*, *>)?.get("openingTime") as? String
                                ?: "N/A",
                            closingTime = (shopData["location"] as? Map<*, *>)?.get("closingTime") as? String
                                ?: "N/A",
                            profileImageUrl = shopData["profileImage"] as? String ?: "",
                            bannerImageUrl = shopData["bannerImage"] as? String ?: "",
                            shopImages = ArrayList(shopImages)



                        )

                        Log.d("ShopRepository", "Fetched shop data: $shop")

                        shopLiveData.value = shop
                        if (shop.profileImageUrl.isNotEmpty()) {
                            saveProfileImageUrlToPrefs(shop.profileImageUrl)
                        } else {
                            // Optionally log that the profileImageUrl is empty and skip overwriting the cached URL
                            Log.d("ShopRepository", "Firestore returned an empty profile image URL; not updating SharedPreferences.")
                        }

                        if (shop.bannerImageUrl.isNotEmpty()){
                            saveBannerImageUrlToPrefs(shop.bannerImageUrl)
                        } else{
                            Log.d("ShopRepository", "Firestore returned an empty profile Banner URL; not updating SharedPreferences.")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching shop data", exception)
            }

        return shopLiveData
    }
    fun getShopId(): String? {
        return sharedPreferences.getString("SHOP_ID", null)
    }
    fun saveProfileImageUrlToPrefs(imageUrl: String) {
       // sharedPreferences.edit().putString("PROFILE_IMAGE_URL", imageUrl).apply()
        prefs.setProfileImageUrl(imageUrl)

    }
    fun getProfileImageUrlFromPrefs(): String? {
     //   return sharedPreferences.getString("PROFILE_IMAGE_URL", "")
        return prefs.getProfileImageUrl()
    }

    fun saveBannerImageUrlToPrefs(imageUrl: String){
       prefs.setBannerImageUrl(imageUrl)
    }
    fun getBannerImageUrlFromPrefs() : String? {
        return prefs.getBannerImageUrl()
    }



}