package com.muhasib.snooq.mvvm.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.model.Product
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.tasks.await
import java.io.File


class UploadProductRepository {
    private lateinit var storage: Storage
    private lateinit var client: Client
    private val firebase = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: MySharedPreferences



    suspend fun uploadProduct(context: Context, productDetails: HashMap<String, Product>): Boolean {
        return try {

            val collectionRef = firebase.collection("products")

            val documentRef = collectionRef.document()
            documentRef.set(productDetails).await()

            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }



    private val uploadedFileIds = mutableMapOf<Int, String>()
    private val Uploaded = MutableLiveData<Boolean>()

    suspend fun uploadImageToAppWrite(context: Context, file: File) {

        client = AppWriteSingleton.getClient(context)
        storage = Storage(client)

        try{
            val fileResponse = storage.createFile(
                bucketId = "6792800d001d344a8d58",
                fileId = ID.unique(),
                file = InputFile.fromFile(file)
            )
            val fileId = fileResponse.id
            val fileUrl = "https://cloud.appwrite.io/v1/storage/buckets/6792800d001d344a8d58/files/$fileId/view?project=677a4b92001bbd3a3742&mode=admin"
            collectShopImageLinks(fileUrl)
            Uploaded.postValue(true)
        }catch (e: AppwriteException) {
            Toast.makeText(context, "Error uploading : ${e.message}", Toast.LENGTH_LONG).show()
            Uploaded.postValue(false)
        }catch (e: Exception){
            Uploaded.postValue(false)
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }


    }
    private fun collectShopImageLinks(url : String){
        Log.d("collectShopImageLinks", "collectShopImageLinks: $url")
    }
}