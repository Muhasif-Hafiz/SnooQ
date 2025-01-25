package com.muhasib.snooq.view.ShopRegistration

import ShopRegistrationViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.muhasib.snooq.R
import com.muhasib.snooq.mvvm.ShopRegistrationRepository
import com.muhasib.snooq.singleton.AppWriteSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayAndPoliciesFragment : Fragment() {

    private lateinit var btnUpload: Button
    private lateinit var shopRegistrationViewModel: ShopRegistrationViewModel
    private lateinit var shopRegistrationRepository: ShopRegistrationRepository


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pay_and_policies, container, false)


        // Initialize the ViewModel and Repository

        shopRegistrationViewModel = ViewModelProvider(requireActivity()).get(ShopRegistrationViewModel::class.java)
        shopRegistrationRepository = ShopRegistrationRepository()

        val editTextBankName: TextInputEditText = view.findViewById(R.id.editTextBankName)
        val editTextAccountNumber: TextInputEditText = view.findViewById(R.id.editTextAccountNumber)
        val editTextIFSC: TextInputEditText = view.findViewById(R.id.editTextIFSC)
        val refund : TextInputEditText= view.findViewById(R.id.editTextRefundPolicy)

        editTextBankName.addTextChangedListener {
            shopRegistrationViewModel.updatePaymentInfo("bankName", it.toString())

        }

        editTextAccountNumber.addTextChangedListener {

            shopRegistrationViewModel.updatePaymentInfo("accountNumber", it.toString())

        }

        editTextIFSC.addTextChangedListener {

            shopRegistrationViewModel.updatePaymentInfo("ifscCode", it.toString())
        }

        refund.addTextChangedListener {

            shopRegistrationViewModel.updatePaymentInfo("refundPolicy", it.toString())
        }




        btnUpload = view.findViewById(R.id.uploadData)
        btnUpload.setOnClickListener {


            uploadData()
        }


        return view
    }

    private fun uploadData() {
        // Validate the fields before proceeding with the upload
        val (isValid, missingFields) = shopRegistrationViewModel.validateFields()





        if (isValid) {
            // Gather all the necessary details from the ViewModel
            val userDetails = shopRegistrationViewModel.userDetailsMap.value ?: hashMapOf()
            val locationDetails = shopRegistrationViewModel.locationDetailsMap.value ?: hashMapOf()
            val paymentDetails = shopRegistrationViewModel.paymentInfoMap.value ?: hashMapOf()

            val shopDetails = hashMapOf<String, String>().apply {
                // Merge the user details
                put("shopName", userDetails["shopName"] ?: "")
                put("shopCategory", userDetails["shopCategory"] ?: "")
                put("ownerName", userDetails["ownerName"] ?: "")
                put("contactNumber", userDetails["contactNumber"] ?: "")
                put("emailAddress", userDetails["emailAddress"] ?: "")
                put("shopDescription", userDetails["shopDescription"] ?: "")

                // Add the location details
                put("fullAddress", locationDetails["fullAddress"] ?: "")
                put("openingHour", locationDetails["openingHour"] ?: "")
                put("closingHour", locationDetails["closingHour"] ?: "")
                put("DeliveryAvailable", locationDetails["DeliveryAvailable"] ?: "")

                // Add the payment details
                put("bankName", paymentDetails["bankName"] ?: "")
                put("accountNumber", paymentDetails["accountNumber"] ?: "")
                put("ifscCode", paymentDetails["ifscCode"] ?: "")
                put("refundPolicy", paymentDetails["refundPolicy"] ?: "")
            }

            // Upload shop details to FireStore
            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    shopRegistrationRepository.uploadShopDetails(shopDetails)
                }

                if (result) {
                    Toast.makeText(requireContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Show error message for missing fields
            val missingFieldsMessage = "The following fields are missing: ${missingFields.joinToString(", ")}"
            Toast.makeText(requireContext(), missingFieldsMessage, Toast.LENGTH_LONG).show()
        }
    }
}
