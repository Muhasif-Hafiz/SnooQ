package com.muhasib.snooq.view.ShopRegistration

import ShopRegistrationViewModel
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.BLACK
import android.os.Build
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ScrollView

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText

import com.muhasib.snooq.R
import com.muhasib.snooq.singleton.AppWriteSingleton

import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class BasicInformationFragment : Fragment() {

    private lateinit var shopName: TextInputEditText
    private lateinit var category: AutoCompleteTextView
    private lateinit var owner: TextInputEditText
    private lateinit var number: TextInputEditText
    private lateinit var emailAddress: TextInputEditText
    private lateinit var shopDescription: TextInputEditText
    private lateinit var viewModel: ShopRegistrationViewModel
    private lateinit var client: Client

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_basic_information, container, false)

        client= AppWriteSingleton.getClient(requireContext())

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ShopRegistrationViewModel::class.java]

        val scrollView: ScrollView = view.findViewById(R.id.scroll_view)
        scrollView.setEdgeEffectColor(Color.GREEN)
        scrollView.isSmoothScrollingEnabled


        // Bind Views
        shopName = view.findViewById(R.id.editTextShopName)
        category = view.findViewById(R.id.autoCompleteCategory)
        owner = view.findViewById(R.id.editTextOwnerName)
        number = view.findViewById(R.id.editTextContactNumber)
        emailAddress = view.findViewById(R.id.editTextEmailAddress)
        shopDescription = view.findViewById(R.id.editTextShopDescription)


        val categories = listOf("Service based", "Product based", "General")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        category.setAdapter(adapter)
        category.dropDownAnchor

        category.setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = parent.getItemAtPosition(position).toString()
            viewModel.updateUserDetails("shopCategory", selectedCategory)
        }

        // Update ViewModel when the text changes in the input fields
        shopName.addTextChangedListener {
            viewModel.updateUserDetails("shopName", it.toString())
        }

        owner.addTextChangedListener {
            viewModel.updateUserDetails("ownerName", it.toString())
        }

        number.addTextChangedListener {
            viewModel.updateUserDetails("contactNumber", it.toString())
        }

        shopDescription.addTextChangedListener {
            viewModel.updateUserDetails("shopDescription", it.toString())
        }

        lifecycleScope.launch {
            try {

                val email: String? = fetchUserEmail()




                if (!email.isNullOrBlank() && isAdded) {
                    withContext(Dispatchers.Main) {
                        viewModel.updateUserDetails("emailAddress", email)
                    }
                } else {

                }
            } catch (e: Exception) {
                if (isAdded) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error fetching email: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    private suspend fun fetchUserEmail() :String {
        val accountService = Account(client)

        try {
            val user = accountService.get()
            val userEmail = user.email

            // Update email in ViewModel
            emailAddress.setText(userEmail)


            return userEmail

        } catch (e: AppwriteException) {
            Toast.makeText(context, "Error fetching user details", Toast.LENGTH_SHORT).show()
        }
        return "No Email Found"
    }
}
