package com.muhasib.snooq.Base

import android.content.Context
import android.content.SharedPreferences



class MySharedPreferences(context : Context){

    companion object{

        val TOKEN = "token"
        val SHOPID = "ShopID"
    }

    private var sharedPreferences : SharedPreferences =
        context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun setToken(token : String){
        editor.putString(TOKEN, token)
        editor.apply()

    }
    fun getToken() : String{
        return sharedPreferences.getString(TOKEN, "")!!
    }

    fun setShopId(shopId : String){
        editor.putString(SHOPID,shopId)
        editor.apply()

    }

    fun getShopId() : String {
        return sharedPreferences.getString(SHOPID,"")!!
    }
    fun clear(){
        editor.clear()
        editor.apply()

    }
    fun setProfileImageUrl(imageUrl: String) {
        editor.putString("PROFILE_IMAGE_URL", imageUrl).apply()
    }
    fun getProfileImageUrl(): String? {
        return sharedPreferences.getString("PROFILE_IMAGE_URL", null)
    }
    fun setBannerImageUrl(imageUrl: String) {
        editor.putString("BANNER_IMAGE_URL", imageUrl).apply()
    }
    fun getBannerImageUrl(): String? {
        return sharedPreferences.getString("BANNER_IMAGE_URL", null)

    }
}