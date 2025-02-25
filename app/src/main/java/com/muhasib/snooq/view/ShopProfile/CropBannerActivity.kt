package com.muhasib.snooq.view.ShopProfile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.ActivityCropBannerBinding
import com.muhasib.snooq.databinding.ActivityCropImageBinding
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CropBannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCropBannerBinding
    private lateinit var client: Client
    private lateinit var storage: Storage

    companion object {
        const val CROP_BANNER_KEY = "uri"
        const val GET_PREVIOUS_BANNER_KEY ="prev_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBannerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        client = AppWriteSingleton.getClient(this)
        storage = Storage(client)

        binding.imgBackCropBannerActivity.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val uri = intent.getStringExtra(CropBannerActivity.CROP_BANNER_KEY)
        val previousImageUri = intent.getStringExtra(CropBannerActivity.GET_PREVIOUS_BANNER_KEY)

        if (!uri.isNullOrEmpty()) {
            binding.cropBannerImageView.setImageUriAsync(Uri.parse(uri))

            binding.imgCheckCropBannerActivity.setOnClickListener {


                if(!previousImageUri.isNullOrEmpty()){
                    saveCropWithUri(previousImageUri)
                }else{
                    saveCrop()
                }


            }
        }

    }
    private fun saveCropWithUri(prev_uri : String) {
        try {
            val bitmap = binding.cropBannerImageView.getCroppedImage()
            if (bitmap == null) {
                Log.e("CropBannerImageActivity", "Failed to get cropped image.")
                return
            }



            binding.progressBarBanner.visibility = View.VISIBLE
            binding.imgCheckCropBannerActivity.isEnabled = false



            val croppedImageFile = saveBitmapToFile(bitmap)

            lifecycleScope.launch {
                deletePreviousBannerImage(prev_uri)
                uploadImageToAppwrite(croppedImageFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun saveCrop() {
        try {
            val bitmap = binding.cropBannerImageView.getCroppedImage()
            if (bitmap == null) {
                Log.e("CropBannerImageActivity", "Failed to get cropped image.")
                return
            }



            binding.progressBarBanner.visibility = View.VISIBLE
            binding.imgCheckCropBannerActivity.isEnabled = false



            val croppedImageFile = saveBitmapToFile(bitmap)
            lifecycleScope.launch {
                uploadImageToAppwrite(croppedImageFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                outputStream.flush()
            }
        } catch (e: Exception) {
            Log.e("CropImageActivity", "Error saving bitmap: ${e.message}")
        }
        return file
    }

    suspend fun uploadImageToAppwrite(file: File) {
        val bucketId = "6792800d001d344a8d58"

        try {
            val fileResponse = storage.createFile(
                bucketId = bucketId,
                fileId = ID.unique(),
                file = InputFile.fromFile(file)
            )
            val fileId = fileResponse.id
            val fileUrl = "https://cloud.appwrite.io/v1/storage/buckets/$bucketId/files/$fileId/view?project=677a4b92001bbd3a3742&mode=admin"

            Log.d("CropImageActivity", "Image uploaded successfully: $fileUrl")
            saveBannerImageUrlToFirestore(fileUrl)

        } catch (e: Exception) {
            Log.e("CropImageActivity", "Appwrite upload failed: ${e.message}")
            binding.progressBarBanner.visibility = View.GONE
            binding.imgBackCropBannerActivity.isEnabled = true
        }
    }
    private fun saveBannerImageUrlToFirestore(fileUrl: String) {
        val shopId = getShopId(this)

        if (shopId.isEmpty()) {
            Log.e("CropImageActivity", "Shop ID is null or empty. Cannot update Firestore.")
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId)
            .update("bannerImage", fileUrl)
            .addOnSuccessListener {
                Log.d("CropImageActivity", "Profile image updated successfully in Firestore")
                val intent = Intent().apply {
                    putExtra("banner_url", fileUrl)
                }
                setResult(RESULT_OK, intent)

                // Hide progress bar
                binding.progressBarBanner.visibility = View.GONE
                binding.imgCheckCropBannerActivity.isEnabled = true

                finish()
            }
            .addOnFailureListener {
                Log.e("CropImageActivity", "Error updating Firestore: ${it.message}")
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
                binding.progressBarBanner.visibility = View.GONE
                binding.imgBackCropBannerActivity.isEnabled = true
            }
    }
    fun getShopId(context: Context): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("SHOP_ID", "") ?: ""
    }
    suspend fun deletePreviousBannerImage(fileUrl: String) {
        val bucketId = "6792800d001d344a8d58"
        val fileId = extractFileIdFromUrl(fileUrl) // Extract fileId from URL

        if (fileId.isEmpty()) {
            Log.e("CropImageActivity", "Invalid file URL: $fileUrl")
            return
        }

        try {
            storage.deleteFile(bucketId, fileId)
            Log.d("CropImageActivity", "Existing image deleted successfully: $fileId")
        } catch (e: Exception) {
            Log.e("CropImageActivity", "Failed to delete existing image: ${e.message}")
        }
    }

    private fun extractFileIdFromUrl(fileUrl: String): String {
        return fileUrl.substringAfterLast("/files/").substringBefore("/view")
    }


}