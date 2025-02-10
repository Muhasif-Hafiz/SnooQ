package com.muhasib.snooq.view.ShopProfile

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muhasib.snooq.databinding.ActivityCropImageBinding
import java.io.File
import java.io.FileOutputStream

class CropImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCropImageBinding

    companion object {
        const val CROP_IMAGE_KEY = "uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCropImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBackCropActivity.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val uri = intent.getStringExtra(CROP_IMAGE_KEY)

        if (uri != null) {
            binding.cropImageView.setImageUriAsync(Uri.parse(uri))
        }

        binding.imgCheckCropActivity.setOnClickListener { saveCrop() }
    }

    private fun saveCrop() {
        try {
            val bitmap = binding.cropImageView.getCroppedImage()
            if (bitmap != null) {
                val croppedImageFile = saveBitmapToFile(bitmap)
                val intent = Intent()
                intent.putExtra("cropped_image_uri", croppedImageFile.toURI().toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val file = File(cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }
}
