package com.muhasib.snooq.view.ShopProfile

import BaseActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.databinding.ActivityCropImageBinding
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CropImageActivity : BaseActivity() {

    private lateinit var binding: ActivityCropImageBinding
    private lateinit var client: Client
    private lateinit var storage: Storage

    companion object {
        const val CROP_IMAGE_KEY = "uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Appwrite Client and Storage
        client = AppWriteSingleton.getClient(this)
        storage = Storage(client)

        binding.imgBackCropActivity.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val uri = intent.getStringExtra(CROP_IMAGE_KEY)
        if (!uri.isNullOrEmpty()) {
            binding.cropImageView.setImageUriAsync(Uri.parse(uri))
        }

        binding.imgCheckCropActivity.setOnClickListener { saveCrop() }
    }

    private fun saveCrop() {
        try {
            val bitmap = binding.cropImageView.getCroppedImage()
            if (bitmap == null) {
                Log.e("CropImageActivity", "Failed to get cropped image.")
                return
            }

            // Show progress bar


            binding.progressBar.visibility = View.VISIBLE
            binding.imgCheckCropActivity.isEnabled = false



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
            saveImageUrlToFirestore(fileUrl)

        } catch (e: Exception) {
            Log.e("CropImageActivity", "Appwrite upload failed: ${e.message}")
            binding.progressBar.visibility = View.GONE
            binding.imgCheckCropActivity.isEnabled = true
        }
    }

    private fun saveImageUrlToFirestore(fileUrl: String) {
        val shopId = getShopId(this)

        if (shopId.isEmpty()) {
            Log.e("CropImageActivity", "Shop ID is null or empty. Cannot update Firestore.")
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId)
            .update("profileImage", fileUrl)
            .addOnSuccessListener {
                Log.d("CropImageActivity", "Profile image updated successfully in Firestore")
                val intent = Intent().apply {
                    putExtra("image_url", fileUrl)
                }
                setResult(RESULT_OK, intent)

                // Hide progress bar
                binding.progressBar.visibility = View.GONE
                binding.imgCheckCropActivity.isEnabled = true

                finish()
            }
            .addOnFailureListener {
                Log.e("CropImageActivity", "Error updating Firestore: ${it.message}")
                binding.progressBar.visibility = View.GONE
                binding.imgCheckCropActivity.isEnabled = true
            }
    }

    fun getShopId(context: Context): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("SHOP_ID", "") ?: ""
    }
}
