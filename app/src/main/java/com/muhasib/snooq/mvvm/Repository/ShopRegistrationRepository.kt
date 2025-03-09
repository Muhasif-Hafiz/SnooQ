package com.muhasib.snooq.mvvm.Repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.Base.MySharedPreferences

import com.muhasib.snooq.model.Shopkeeper

import kotlinx.coroutines.tasks.await

import kotlin.collections.HashMap


class ShopRegistrationRepository{



    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: MySharedPreferences

    // Upload shop details to the database
    suspend fun uploadShopDetails(context: Context,shopDetails: HashMap<String, Shopkeeper>): Boolean {
        return try {
            val collectionRef = firestore.collection("shops")
            sharedPreferences= MySharedPreferences(context)


            val documentRef = collectionRef.document()
            documentRef.set(shopDetails).await()
            saveShopId(context, documentRef.id)
            sharedPreferences.setShopId(documentRef.id)




            true
        } catch (e: Exception) {
            // Handle exceptions and log the error properly
            e.printStackTrace()
            false
        }
    }
    fun saveShopId(context: Context, shopId: String) {

        sharedPreferences= MySharedPreferences(context)
        sharedPreferences.setShopId(shopId)
        Log.d("UserIdShopkeeper",shopId)
        Log.d("UserIdShopkeeper", sharedPreferences.getShopId())
    }

}
