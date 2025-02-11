package com.muhasib.snooq.view.ShopProfile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.muhasib.snooq.R

import com.muhasib.snooq.databinding.FragmentShopkeeperProfileBinding
import com.muhasib.snooq.singleton.AppWriteSingleton
import com.muhasib.snooq.viewModel.shopViewModel
import io.appwrite.Client
import io.appwrite.services.Storage

class ShopkeeperProfileFragment : Fragment(R.layout.fragment_shopkeeper_profile) {

    private var _binding: FragmentShopkeeperProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var client: Client
    private lateinit var storage: Storage
    private val viewModel: shopViewModel by viewModels()

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentShopkeeperProfileBinding.bind(requireView())

        val toolbar = binding.toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        client = AppWriteSingleton.getClient(requireContext())
        storage = Storage(client)

        val cachedImageUrl = viewModel.getProfileImageUrlFromPrefs()

        if (!cachedImageUrl.isNullOrEmpty()) {
            setProfilePicture(cachedImageUrl, binding.profileImageShopActivity)
            observeShopData()
        } else {
            Log.d("ShopFragment", "No cached image found, trying to fetch from Firestore...")
            observeShopData()
        }

        binding.profileImageShopActivity.setOnClickListener {


            showOptions()
        }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val uri = result.data?.data
                val intent = Intent(requireContext(), CropImageActivity::class.java)
                intent.putExtra(CropImageActivity.CROP_IMAGE_KEY, uri.toString())
                getCroppedImage.launch(intent)
            }
        }

    private val getCroppedImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val imageUrl = result.data!!.getStringExtra("image_url")
                if (imageUrl != null) {
                    loadProfileImage(imageUrl)
                    viewModel.saveProfileImageUrlToPrefs(imageUrl)
                }
            }
        }

    private fun setProfilePicture(imageUrl: String, imageView: ImageView) {
        Log.d("Glide", "Loading Image: $imageUrl")

        imageView.apply {
            visibility = ImageView.VISIBLE
            Glide.with(this@ShopkeeperProfileFragment)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_picture)
                .error(R.drawable.ic_error)
                .into(this)
        }
    }

    private fun observeShopData() {
        viewModel.shopData.observe(viewLifecycleOwner) { shop ->
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
            .placeholder(R.drawable.ic_profile_picture)
            .error(R.drawable.ic_error)
            .into(binding.profileImageShopActivity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showOptions(){

        val dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(R.layout.bottom_sheet_update_profile, null)

        val btnUpload= view.findViewById<LinearLayout>(R.id.upload_photo_option)
        val btnViewProfile =  view.findViewById<LinearLayout>(R.id.view_photo_option)

        btnUpload.setOnClickListener {



            if (isAdded) {
                Log.d("ShopFragment", "Profile image clicked")

                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Intent(MediaStore.ACTION_PICK_IMAGES)
                } else {
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                }

                try {
                    pickImage.launch(intent)
                    Log.d("ShopFragment", "pickImage launched successfully")
                } catch (e: Exception) {
                    Log.e("ShopFragment", "Error launching image picker", e)
                    Toast.makeText(
                        requireContext(),
                        "Failed to open image picker",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }

        }

         btnViewProfile.setOnClickListener {

             Toast.makeText(requireContext(),"Soon", Toast.LENGTH_SHORT).show()

         }



        dialog.setCancelable(true)


        dialog.setContentView(view)


        dialog.show()
    }
}
