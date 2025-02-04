package com.muhasib.snooq.view.ShopRegistration

import ShopRegistrationViewModel
import UploadData
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.muhasib.snooq.R
import com.muhasib.snooq.mvvm.ShopRegistrationRepository

class PayAndPoliciesFragment : Fragment() {


    private lateinit var chipGroup: ChipGroup
    private lateinit var shopRegistrationViewModel: ShopRegistrationViewModel
    private lateinit var shopRegistrationRepository: ShopRegistrationRepository
    lateinit var checkBoxTerms: MaterialCheckBox
    lateinit var checkBoxPrivacy: MaterialCheckBox

   // lateinit var bottomNavigationView: BottomNavigationView



    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pay_and_policies, container, false)

        val scrollView: ScrollView = view.findViewById(R.id.Scroll_View_payment)


            scrollView.setEdgeEffectColor(Color.GREEN)


        shopRegistrationViewModel =
            ViewModelProvider(requireActivity()).get(ShopRegistrationViewModel::class.java)
        shopRegistrationRepository = ShopRegistrationRepository()

        val editTextBankName: TextInputEditText = view.findViewById(R.id.editTextBankName)
        val editTextAccountNumber: TextInputEditText = view.findViewById(R.id.editTextAccountNumber)
        val editTextIFSC: TextInputEditText = view.findViewById(R.id.editTextIFSC)
        val refund: TextInputEditText = view.findViewById(R.id.editTextRefundPolicy)

        checkBoxTerms = view.findViewById(R.id.checkBoxTerms)
        checkBoxPrivacy = view.findViewById(R.id.checkBoxPrivacy)

        val termsText = "I agree to the platform's terms and conditions"
        val privacyText = "I accept the privacy policy"
        val termsSpannable = SpannableString(termsText)
        val privacySpannable = SpannableString(privacyText)


        val softBlueColor = ContextCompat.getColor(requireContext(), R.color.soft_blue) // Replace with your actual color


        termsSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {

                openTermsAndCondition()

            }


        }, termsText.indexOf("terms and conditions"), termsText.indexOf("terms and conditions") + "terms and conditions".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        privacySpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                openPrivacyPolicySheet()
                //Toast.makeText(requireContext(), " Opening Privacy Policy", Toast.LENGTH_SHORT).show()
            }
        }, privacyText.indexOf("privacy policy"), privacyText.indexOf("privacy policy") + "privacy policy".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        termsSpannable.setSpan(ForegroundColorSpan(softBlueColor), termsText.indexOf("terms and conditions"), termsText.indexOf("terms and conditions") + "terms and conditions".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        privacySpannable.setSpan(ForegroundColorSpan(softBlueColor), privacyText.indexOf("privacy policy"), privacyText.indexOf("privacy policy") + "privacy policy".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        checkBoxTerms.text = termsSpannable
        checkBoxPrivacy.text = privacySpannable

        checkBoxTerms.movementMethod = LinkMovementMethod.getInstance()
        checkBoxPrivacy.movementMethod = LinkMovementMethod.getInstance()







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


        chipGroup = view.findViewById(R.id.chipGroupPaymentMethods)


        val checkedDaysList = arrayListOf<String>()
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            checkedDaysList.clear()
            checkedIds.forEach { idx ->
                val chip = view.findViewById<Chip>(idx)

                checkedDaysList.add(chip.text.toString())
            }
            shopRegistrationViewModel.updatePaymentInfo(
                "paymentMethod",
                checkedDaysList.joinToString(",")
            )

        }




        return view
    }
    @SuppressLint("MissingInflatedId")
    private fun openTermsAndCondition() {
        val dialog = BottomSheetDialog(requireContext())

        val view= layoutInflater.inflate(R.layout.fragment_terms_and_conditions, null)

        val btnClose = view.findViewById<Button>(R.id.accept_button_terms)

        btnClose.setOnClickListener {

            dialog.dismiss()
        }

        dialog.setCancelable(false)


        dialog.setContentView(view)

        dialog.show()


    }

     @SuppressLint("MissingInflatedId")
     private fun openPrivacyPolicySheet(){


         val dialog= BottomSheetDialog(requireContext())

         val view = layoutInflater.inflate(R.layout.fragment_privacy_policy, null)

         val btnClose = view.findViewById<Button>(R.id.accept_button_privacy)

         btnClose.setOnClickListener {

             dialog.dismiss()
         }

         dialog.setCancelable(false)


         dialog.setContentView(view)

         dialog.show()

     }
    fun areCheckBoxesChecked(): Boolean {
        return checkBoxTerms.isChecked && checkBoxPrivacy.isChecked
    }
}

