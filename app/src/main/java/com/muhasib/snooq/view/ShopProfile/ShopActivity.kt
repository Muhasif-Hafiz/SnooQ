package com.muhasib.snooq.view.ShopProfile

import BaseActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.ActivityShopBinding
import com.muhasib.snooq.singleton.AppWriteSingleton
import com.muhasib.snooq.viewModel.shopViewModel
import io.appwrite.Client
import io.appwrite.services.Storage
import java.io.File
import java.io.FileInputStream


class ShopActivity : BaseActivity() {


    private lateinit var binding: ActivityShopBinding
    private lateinit var client: Client
    private lateinit var storage: Storage
    private val viewModel: shopViewModel by viewModels()





    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)




        client = AppWriteSingleton.getClient(this)
        storage = Storage(client)

        val cachedImageUrl = viewModel.getProfileImageUrlFromPrefs()


        if (!cachedImageUrl.isNullOrEmpty()) {
            setProfilePicture(cachedImageUrl, binding.profileImageShopActivity)
            observeShopData()
        } else {
            Log.d("ShopActivity", "No cached image found, trying to fetch from Firestore...")
            // If no cached image, fetch from Firestore
            observeShopData()
        }

       // loadUserData(shop_id)
        binding.profileImageShopActivity.setOnClickListener {
            if (!isFinishing && !isDestroyed) {
                Log.d("ShopActivity", "Profile image clicked")

                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
                    Intent(MediaStore.ACTION_PICK_IMAGES)
                } else {
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                }

                try {
                    pickImage.launch(intent)
                    Log.d("ShopActivity", "pickImage launched successfully")
                } catch (e: Exception) {
                    Log.e("ShopActivity", "Error launching image picker", e)
                    Toast.makeText(this, "Failed to open image picker", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }



    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val uri = result.data?.data
            val intent = Intent(this@ShopActivity, CropImageActivity::class.java)
            intent.putExtra(CropImageActivity.CROP_IMAGE_KEY, uri.toString())
            getCroppedImage.launch(intent)
        }
    }


    private val getCroppedImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageUrl = result.data!!.getStringExtra("image_url")
            if (imageUrl != null) {
                loadProfileImage(imageUrl)
                viewModel.saveProfileImageUrlToPrefs(imageUrl)
            }
        }
    }

//    private fun loadUserData(shop_id: String) {
//        val db = FirebaseFirestore.getInstance()
//
//        Log.d("Firestore", "Fetching data for SHOP_ID: $shop_id")
//
//        db.collection("shops").document(shop_id)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    Log.d("Firestore", "Document data: ${document.data}")
//
//                    val shopkeeperData = document.data?.values?.firstOrNull() as? Map<*, *>
//                    val shopkeeperName = shopkeeperData?.get("name") as? String ?: "N/A"
//                    val shopkeeperEmail = shopkeeperData?.get("email") as? String ?: "N/A"
//                    val shopsList = shopkeeperData?.get("shops") as? List<Map<*, *>>
//
//                    if (shopsList != null && shopsList.isNotEmpty()) {
//                        val shopData = shopsList[0]  // Get first shop
//
//                        val shopName = shopData["shopName"] as? String ?: "N/A"
//                        val shopDescription = shopData["description"] as? String ?: "N/A"
//                        val address = shopData["address"] as? String ?: "N/A"
//                        val profileImageUrl = shopData["profileImage"] as? String ?: ""
//
//                        val location = shopData["location"] as? Map<*, *>
//                        val openingTime = location?.get("openingTime") as? String ?: "N/A"
//                        val closingTime = location?.get("closingTime") as? String ?: "N/A"
//
//                        runOnUiThread {
//                            binding.shopNameShopActivity.text = shopName
//                            binding.shopOwnerNameShopActivity.text = shopkeeperName
//                            binding.shopDescriptionShopActivity.text = shopDescription
//                            binding.shopAddressShopActivity.text = address
//                            binding.textOpenTimeShopActivity.text = openingTime
//                            binding.closingTimeShopActivity.text = closingTime
//
//                            // Load profile image from cache or Firestore
//                            val cachedImageUrl = viewModel.getProfileImageUrlFromPrefs()
//                            if (!cachedImageUrl.isNullOrEmpty()) {
//                              //  binding.profileImageShopActivity.setImageURI(Uri.parse(cachedImageUrl))
//                                setProfilePicture(cachedImageUrl , binding.profileImageShopActivity)
//                            } else if (!profileImageUrl.isNullOrEmpty()) {
//                                Log.d("ShopActivity", "Profile Image URL: $profileImageUrl")
//                                setProfilePicture(profileImageUrl, binding.profileImageShopActivity)
//
//                                viewModel.saveProfileImageUrlToPrefs(profileImageUrl) // Save for future use
//                            }
//                        }
//                    } else {
//                        Log.e("Firestore", "No shop data found")
//                    }
//                } else {
//                    Log.d("Firestore", "Document does not exist.")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.e("Firestore", "Error fetching shop data", exception)
//            }
//    }





    private fun setProfilePicture(imageUrl: String, imageView: ImageView) {
        Log.d("Glide", "Loading Image: $imageUrl")

        imageView.apply {
            visibility = ImageView.VISIBLE
            Glide.with(this@ShopActivity)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(this)
        }

    }
    private fun observeShopData() {
        viewModel.shopData.observe(this) { shop ->
            binding.shopNameShopActivity.text = shop.shopName
            binding.shopOwnerNameShopActivity.text = shop.shopkeeperName
            binding.shopDescriptionShopActivity.text = shop.shopDescription
            binding.shopAddressShopActivity.text = shop.address
            binding.textOpenTimeShopActivity.text = shop.openingTime
            binding.closingTimeShopActivity.text = shop.closingTime

            val profileImageUrl = shop.profileImageUrl ?: viewModel.getProfileImageUrlFromPrefs()
            if (!profileImageUrl.isNullOrEmpty()) {
                loadProfileImage(profileImageUrl)
            }
        }
    }


    private fun loadProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.profileImageShopActivity)
    }



}
