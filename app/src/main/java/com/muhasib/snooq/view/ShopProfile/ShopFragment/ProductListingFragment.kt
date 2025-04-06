package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.FragmentProductListingBinding
import com.muhasib.snooq.remote.RetrofitClient
import com.muhasib.snooq.mvvm.Repository.ProductListingRepository
import com.muhasib.snooq.mvvm.ViewModel.ProductListingViewModel
import com.muhasib.snooq.mvvm.ViewModel.ProductListingViewModelFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class ProductListingFragment : Fragment() {

    private var _binding: FragmentProductListingBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraImageFile: File
    private lateinit var cameraImageUri: Uri
    private lateinit var viewModel: ProductListingViewModel
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 2
    private val REQUEST_GALLERY_PICK = 3
    private lateinit var  sharedPreferences: MySharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_product_listing, container, false)
        _binding = FragmentProductListingBinding.bind(view)


        sharedPreferences = MySharedPreferences(requireContext())
        val factory = ProductListingViewModelFactory(ProductListingRepository(RetrofitClient.instance))
        viewModel = ViewModelProvider(this, factory).get(ProductListingViewModel::class.java)

        binding.pickImageButton.setOnClickListener {
            showImageSourceDialog()
        }

//        viewModel.processedImage.observe(viewLifecycleOwner, Observer { bitmap ->
//            binding.ProductImage1.setImageBitmap(bitmap)
//        })
//

        // Observe the loading state
//        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
//            if (isLoading) {
//                Toast.makeText(requireContext(), "Processing Image...", Toast.LENGTH_SHORT).show()
//                binding.progressBar.visibility=View.VISIBLE
//            } else {
//                binding.progressBar.visibility=View.GONE
//            }
//        })
//
//
//
//


        return view
    }
    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> pickImageFromGallery()
                }
            }
            .show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            dispatchTakePictureIntent()
        }
    }
    private fun dispatchTakePictureIntent() {
        val photoFile = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        cameraImageFile = photoFile
        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }


    // Open Gallery
    private fun pickImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_GALLERY_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    try {
                        // Try to use the URI
                        if (::cameraImageUri.isInitialized) {
                            val compressedFile = compressImage(cameraImageUri)
                            lifecycleScope.launch {
                                viewModel.AppWriteRepository.uploadImageToAppWrite(requireContext(), compressedFile)
                            }
                        } else {
                            throw UninitializedPropertyAccessException("cameraImageUri not initialized")
                        }
                    } catch (e: Exception) {
                        // Fallback: use thumbnail bitmap
                        val bitmap = data?.extras?.get("data") as? Bitmap
                        if (bitmap != null) {
                            val uri = bitmapToUri(requireContext(), bitmap)
                            if (uri != null) {
                                val compressedFile = compressImage(uri)
                                lifecycleScope.launch {
                                    viewModel.AppWriteRepository.uploadImageToAppWrite(requireContext(), compressedFile)
                                }
                            } else {
                                Toast.makeText(requireContext(), "Image conversion failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Image capture failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                REQUEST_GALLERY_PICK -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val compressedFile = compressImage(imageUri)
                        lifecycleScope.launch {
                            viewModel.AppWriteRepository.uploadImageToAppWrite(requireContext(), compressedFile)
                        }
                    }
                }
            }
        }
    }


    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val wrapper = ContextWrapper(context)
        val directory = wrapper.getDir("images", Context.MODE_PRIVATE)
        val file = File(directory, "${UUID.randomUUID()}.jpg")

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun compressImage(uri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
        val file = File(context?.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // 50% quality
        outputStream.flush()
        outputStream.close()
        return file
    }
}
