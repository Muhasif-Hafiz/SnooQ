package com.muhasib.snooq.view.ShopRegistration

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.dynamicfeatures.Constants
import com.muhasib.snooq.R
import com.muhasib.snooq.R.id.btnUploadPhoto2
import com.muhasib.snooq.constants.ClientConstants
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.launch
import java.io.File

class ShopMediaFragment : Fragment() {

    // ImageButton IDs and their corresponding selected URIs
    private lateinit var btnUploadPhoto1: ImageButton
    private lateinit var btnUploadPhoto2: ImageButton
    private lateinit var btnUploadPhoto3: ImageButton
    private lateinit var btnUploadPhoto4: ImageButton
    private lateinit var btnUploadLogo: ImageButton
    private lateinit var btnUploadBanner: ImageButton
    private lateinit var btnUploadBusinessProof: ImageButton
    private lateinit var btnUploadIDProof: ImageButton

    private lateinit var btnUploadAll: Button
    private lateinit var client: Client
    private lateinit var storage: Storage

    private val selectedImages = mutableMapOf<Int, Uri>()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val buttonId = currentButtonId
            if (buttonId != null) {
                selectedImages[buttonId] = it
                view?.findViewById<ImageButton>(buttonId)?.setImageURI(it) // Update the ImageButton
            }
        } ?: Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
    }

    private var currentButtonId: Int? = null // Keeps track of the currently clicked ImageButton

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
        btnUploadLogo = view.findViewById(R.id.btnUploadLogo)
        btnUploadBanner = view.findViewById(R.id.btnUploadBanner)
        btnUploadBusinessProof = view.findViewById(R.id.btnUploadBusinessProof)
        btnUploadIDProof = view.findViewById(R.id.btnUploadIDProof)

        // Initialize Upload button
        btnUploadAll = view.findViewById(R.id.btnUploadToAppwrite)

        // Set click listeners for ImageButtons
        setImageButtonClickListener(btnUploadPhoto1)
        setImageButtonClickListener(btnUploadPhoto2)
        setImageButtonClickListener(btnUploadPhoto3)
        setImageButtonClickListener(btnUploadPhoto4)
        setImageButtonClickListener(btnUploadLogo)
        setImageButtonClickListener(btnUploadBanner)
        setImageButtonClickListener(btnUploadBusinessProof)
        setImageButtonClickListener(btnUploadIDProof)

        // Set click listener for Upload button
        btnUploadAll.setOnClickListener {
            if (selectedImages.isNotEmpty()) {
                lifecycleScope.launch {
                    uploadAllImagesToAppwrite()
                }
            } else {
                Toast.makeText(requireContext(), "Please select at least one image", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun setImageButtonClickListener(button: ImageButton) {
        button.setOnClickListener {
            currentButtonId = button.id
            pickImage.launch("image/*")
        }
    }

    private suspend fun uploadAllImagesToAppwrite() {
        for ((buttonId, uri) in selectedImages) {
            val fileName = when (buttonId) {
                R.id.btnUploadPhoto1 -> "shop_photo_1.jpg"
                R.id.btnUploadPhoto2 -> "shop_photo_2.jpg"
                R.id.btnUploadPhoto3 -> "shop_photo_3.jpg"
                R.id.btnUploadPhoto4 -> "shop_photo_4.jpg"
                R.id.btnUploadLogo -> "shop_logo.jpg"
                R.id.btnUploadBanner -> "shop_banner.jpg"
                R.id.btnUploadBusinessProof -> "business_proof.jpg"
                R.id.btnUploadIDProof -> "id_proof.jpg"
                else -> "unknown_file.jpg"
            }

            try {
                val file = File(requireContext().cacheDir, fileName).apply {
                    outputStream().use { output ->
                        requireContext().contentResolver.openInputStream(uri)?.use { input ->
                            input.copyTo(output)
                        }
                    }
                }

          val fileResponse=      storage.createFile(
                    bucketId = "6792800d001d344a8d58",
                    fileId = ID.unique(),
                    file = InputFile.fromFile(file)
                )
                val fileId= fileResponse.id
                val fileUrl = "https://cloud.appwrite.io/v1/storage/buckets//files/$fileId/view"






                Toast.makeText(requireContext(), "$fileName uploaded successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: AppwriteException) {
                Toast.makeText(requireContext(), "Error uploading $fileName: ${e.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unexpected error for $fileName: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
