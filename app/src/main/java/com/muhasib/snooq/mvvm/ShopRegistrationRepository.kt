package com.muhasib.snooq.mvvm

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore

import com.muhasib.snooq.model.Shopkeeper

import kotlinx.coroutines.tasks.await

import kotlin.collections.HashMap


class ShopRegistrationRepository{



    private val firestore = FirebaseFirestore.getInstance()

    // Upload shop details to the database
    suspend fun uploadShopDetails(context: Context,shopDetails: HashMap<String, Shopkeeper>): Boolean {
        return try {
            val collectionRef = firestore.collection("shops")


            val documentRef = collectionRef.document()
            documentRef.set(shopDetails).await()
            saveShopId(context, documentRef.id)




            true
        } catch (e: Exception) {
            // Handle exceptions and log the error properly
            e.printStackTrace()
            false
        }
    }
    fun saveShopId(context: Context, shopId: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("SHOP_ID", shopId).apply()
    }

}
