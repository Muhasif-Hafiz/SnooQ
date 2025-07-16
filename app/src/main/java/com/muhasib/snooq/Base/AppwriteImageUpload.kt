package com.muhasib.snooq.Base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import java.io.File
import java.io.FileOutputStream

class AppwriteImageUpload {

    suspend fun uploadImageTo(file: File, context: Context): String? {
        return try {
            val compressedFile = compressImage(file, context)

            val client = AppWriteSingleton.getClient(context)
            val storage = Storage(client)
            val bucketId = "6792800d001d344a8d58"

            val response = storage.createFile(
                bucketId = bucketId,
                fileId = ID.unique(),
                file = InputFile.fromFile(compressedFile)
            )

            val fileId = response.id
            val fileUrl =
                "https://cloud.appwrite.io/v1/storage/buckets/$bucketId/files/$fileId/view?project=677a4b92001bbd3a3742&mode=admin"

            Log.d("AppwriteUpload", "Image uploaded successfully: $fileUrl")
            fileUrl

        } catch (e: Exception) {
            Log.e("AppwriteUpload", "Upload failed: ${e.message}")
            Toast.makeText(context, "Image size too big!", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun compressImage(file: File, context: Context): File {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(file.absolutePath, options)

        // Calculate sample size to avoid OOM
        options.inSampleSize = calculateInSampleSize(options, 800, 800)
        options.inJustDecodeBounds = false

        val originalBitmap = BitmapFactory.decodeFile(file.absolutePath, options)
            ?: throw IllegalArgumentException("Failed to decode image: ${file.name}")

        // Resize and compress
        val compressedFile = File(context.cacheDir, "compressed_${file.name}")
        FileOutputStream(compressedFile).use { outputStream ->
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
        }

        Log.d("Compression", "Original: ${file.length() / 1024} KB | Compressed: ${compressedFile.length() / 1024} KB")

        return compressedFile
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

}
