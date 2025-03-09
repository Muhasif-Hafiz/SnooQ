package com.muhasib.snooq.mvvm.Repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.model.Product
import kotlinx.coroutines.tasks.await

class UploadProductRepository {

   private  val firebase = FirebaseFirestore.getInstance()
    private lateinit var  sharedPreferences: MySharedPreferences


   suspend fun uploadProduct(context : Context, productDetails : HashMap<String, Product>) : Boolean{
        return try {

            val collectionRef = firebase.collection("products")

            val documentRef = collectionRef.document()
            documentRef.set(productDetails).await()

            true

        }catch (e : Exception){
            e.printStackTrace()
            false
        }

    }
}