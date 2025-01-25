package com.muhasib.snooq.mvvm

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await

import java.util.HashMap


class ShopRegistrationRepository{



    private val firestore = FirebaseFirestore.getInstance()

    // Upload shop details to the database
    suspend fun uploadShopDetails(shopDetails: HashMap<String, String>): Boolean {
        return try {
            val collectionRef = firestore.collection("shops")


            val documentRef = collectionRef.document()
            documentRef.set(shopDetails).await()


            true
        } catch (e: Exception) {
            // Handle exceptions and log the error properly
            e.printStackTrace()
            false
        }
    }

}
