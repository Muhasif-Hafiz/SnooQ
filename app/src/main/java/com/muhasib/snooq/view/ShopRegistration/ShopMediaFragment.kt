package com.muhasib.snooq.view.ShopRegistration

import ShopRegistrationViewModel
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.muhasib.snooq.R
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ShopMediaFragment : Fragment() {

    // ImageButton IDs and their corresponding selected URIs
    private lateinit var btnUploadPhoto1: ImageButton
    private lateinit var btnUploadPhoto2: ImageButton
    private lateinit var btnUploadPhoto3: ImageButton
    private lateinit var btnUploadPhoto4: ImageButton
    private lateinit var btnUploadPhoto5: ImageButton
    private lateinit var btnUploadPhoto6: ImageButton
    private  lateinit var  instagram : EditText
    private lateinit var  facebook : EditText
    private lateinit var  otherLinks : EditText



    private lateinit var client: Client
    private lateinit var storage: Storage
    private  lateinit var  viewModel : ShopRegistrationViewModel

    private val selectedImages = mutableMapOf<Int, Uri>()

    private val uploadedFileIds = mutableMapOf<Int, String>()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val buttonId = currentButtonId
            if (buttonId != null) {
                selectedImages[buttonId] = it
                view?.findViewById<ImageButton>(buttonId)?.setImageURI(it) // Update ImageButton

                // Upload new image and replace the existing one if needed
                lifecycleScope.launch {
                    uploadImageToAppwrite(buttonId, it)
                }
            }
        } ?: Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
    }

    private var currentButtonId: Int? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_media, container, false)

        // Initialize Appwrite client and storage
        client = AppWriteSingleton.getClient(requireContext())
        storage = Storage(client)

        // Initialize ImageButtons
        btnUploadPhoto1 = view.findViewById(R.id.btnUploadPhoto1)
        btnUploadPhoto2 = view.findViewById(R.id.btnUploadPhoto2)
        btnUploadPhoto3 = view.findViewById(R.id.btnUploadPhoto3)
        btnUploadPhoto4 = view.findViewById(R.id.btnUploadPhoto4)
        btnUploadPhoto5 = view.findViewById(R.id.btnUploadPhoto5)
        btnUploadPhoto6 = view.findViewById(R.id.btnUploadPhoto6)
        instagram  = view.findViewById<EditText>(R.id.insta_link)
        facebook = view.findViewById(R.id.facebook_link)
        otherLinks = view.findViewById( R.id.other_link)


        viewModel = ViewModelProvider(requireActivity())[ShopRegistrationViewModel::class.java]

        // Set click listeners for ImageButtons
        setImageButtonClickListener(btnUploadPhoto1)
        setImageButtonClickListener(btnUploadPhoto2)
        setImageButtonClickListener(btnUploadPhoto3)
        setImageButtonClickListener(btnUploadPhoto4)
        setImageButtonClickListener(btnUploadPhoto5)
        setImageButtonClickListener(btnUploadPhoto6)
        val scrollView: ScrollView = view.findViewById(R.id.scrollView)
        scrollView.setEdgeEffectColor(Color.GREEN)
        scrollView.isSmoothScrollingEnabled


        instagram.addTextChangedListener {
            collectAndSaveLinks()
        }

        facebook.addTextChangedListener {
            collectAndSaveLinks()
        }

        otherLinks.addTextChangedListener {
            collectAndSaveLinks()
        }




        return view
    }

    private fun setImageButtonClickListener(button: ImageButton) {
        button.setOnClickListener {


            currentButtonId = button.id
            pickImage.launch("image/*")
        }
    }

    private suspend fun uploadImageToAppwrite(buttonId: Int, uri: Uri) {
        val fileName = when (buttonId) {
            R.id.btnUploadPhoto1 -> "shop_photo_1.jpg"
            R.id.btnUploadPhoto2 -> "shop_photo_2.jpg"
            R.id.btnUploadPhoto3 -> "shop_photo_3.jpg"
            R.id.btnUploadPhoto4 -> "shop_photo_4.jpg"
            R.id.btnUploadPhoto5 -> "shop_photo_5.jpg"
            R.id.btnUploadPhoto6 -> "shop_photo_6.jpg"
            else -> "unknown_file.jpg"
        }

        try {
            val compressedFile = compressImage(uri)

            // Delete previous file if exists
            uploadedFileIds[buttonId]?.let { oldFileId ->
                try {
                    storage.deleteFile("6792800d001d344a8d58", oldFileId)
                } catch (e: AppwriteException) {
                    Toast.makeText(requireContext(), "Please re-upload this image!", Toast.LENGTH_SHORT).show()
                }
            }

            val fileResponse = storage.createFile(
                bucketId = "6792800d001d344a8d58",
                fileId = ID.unique(),
                file = InputFile.fromFile(compressedFile)
            )

            val fileId = fileResponse.id
            val fileUrl = "https://cloud.appwrite.io/v1/storage/buckets/6792800d001d344a8d58/files/$fileId/view?project=677a4b92001bbd3a3742&mode=admin"
            collectShopImageLinks(fileUrl)

            // Save file ID on success
            uploadedFileIds[buttonId] = fileId

        } catch (e: AppwriteException) {
            Toast.makeText(requireContext(), "Error uploading $fileName: ${e.message}", Toast.LENGTH_LONG).show()

            // Remove image from ImageButton on failure
            activity?.runOnUiThread {
                view?.findViewById<ImageButton>(buttonId)?.setImageResource(R.drawable.ic_failed)
            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unexpected error for $fileName: ${e.message}", Toast.LENGTH_LONG).show()

            // Remove image from ImageButton on failure
            activity?.runOnUiThread {
                view?.findViewById<ImageButton>(buttonId)?.setImageResource(R.drawable.ic_failed)
            }
        }
    }

    private fun collectAndSaveLinks() {
        val instagramLink = instagram.text.toString()
        val facebookLink = facebook.text.toString()
        val otherLink = otherLinks.text.toString()

        viewModel.addSocialMediaLink(instagramLink, facebookLink, otherLink)
    }
    private fun collectShopImageLinks(link : String){

        viewModel.addShopImageLinks(link)

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
