package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.muhasib.snooq.R
import com.muhasib.snooq.adapters.StockAdapter
import com.muhasib.snooq.databinding.FragmentProductListingBinding
import com.muhasib.snooq.remote.RetrofitClient
import com.muhasib.snooq.repository.ProductListingRepository
import com.muhasib.snooq.viewModel.ProductListingViewModel
import com.muhasib.snooq.viewModel.ProductListingViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ProductListingFragment : Fragment() {

    private var _binding: FragmentProductListingBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductListingViewModel
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 2
    private val REQUEST_GALLERY_PICK = 3
    private lateinit var adapter: StockAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_product_listing, container, false)
        _binding = FragmentProductListingBinding.bind(view)

        // Create ViewModel instance
        val factory = ProductListingViewModelFactory(ProductListingRepository(RetrofitClient.instance))
        viewModel = ViewModelProvider(this, factory).get(ProductListingViewModel::class.java)

        binding.btnPickImage.setOnClickListener {
            showImageSourceDialog()
        }

        // Observe the processed image from ViewModel
        viewModel.processedImage.observe(viewLifecycleOwner, Observer { bitmap ->
            binding.imageViewProduct.setImageBitmap(bitmap)
        })

        // Observe the loading state
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                Toast.makeText(requireContext(), "Processing Image...", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility=View.VISIBLE
            } else {
                binding.progressBar.visibility=View.GONE
            }
        })

        // Observe error state
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Log.e("Error", error)
        })
        /// Tab layout Managerment
        adapter = StockAdapter(childFragmentManager, lifecycle)
        binding.TabLayoutStock.addTab(binding.TabLayoutStock.newTab().setText("Upload Photos"))
        binding.TabLayoutStock.addTab(binding.TabLayoutStock.newTab().setText("Scan Product"))

        binding.ViewPager2Stock.adapter = adapter

        binding.TabLayoutStock.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {

                if (p0 != null) {
                    binding.ViewPager2Stock.currentItem = p0.position
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })
        binding.ViewPager2Stock.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.TabLayoutStock.selectTab(binding.TabLayoutStock.getTabAt(position))
            }
        })



        return view
    }

    // Show dialog to choose between Camera and Gallery
    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()  // Take Photo
                    1 -> pickImageFromGallery()
                }
            }
            .show()
    }

    // Request Camera Permission
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            dispatchTakePictureIntent()
        }
    }

    // Open Camera
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
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
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    removeBackground(imageBitmap)
                }
                REQUEST_GALLERY_PICK -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        val imageBitmap = uriToBitmap(it)
                        removeBackground(imageBitmap)
                    }
                }
            }
        }
    }

    // Convert Uri to Bitmap
    private fun uriToBitmap(imageUri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        }
    }

    // Remove Background
    private fun removeBackground(imageBitmap: Bitmap) {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        val outputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image_file", file.name, requestBody)

        viewModel.removeBackground(part)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
