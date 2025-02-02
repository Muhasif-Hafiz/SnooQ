package com.muhasib.snooq.view.ShopRegistration

import ShopRegistrationViewModel
import UploadData
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.muhasib.snooq.R
import com.muhasib.snooq.mvvm.ShopRegistrationRepository

class PayAndPoliciesFragment : Fragment() {

    private lateinit var btnUpload: Button
    private lateinit var shopRegistrationViewModel: ShopRegistrationViewModel
    private lateinit var shopRegistrationRepository: ShopRegistrationRepository


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pay_and_policies, container, false)


        // Initialize the ViewModel and Repository
        val scrollView: ScrollView = view.findViewById(R.id.Scroll_View_payment)
        scrollView.setEdgeEffectColor(Color.GREEN)
        scrollView.isSmoothScrollingEnabled

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


            UploadData(requireContext(), lifecycleOwner = viewLifecycleOwner, shopRegistrationViewModel, shopRegistrationRepository).uploadData()

        }


        return view
    }


}
