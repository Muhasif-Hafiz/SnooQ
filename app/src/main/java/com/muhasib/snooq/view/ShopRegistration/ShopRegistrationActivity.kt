package com.muhasib.snooq.view.ShopRegistration

import BaseActivity
import ShopRegistrationViewModel
import UploadData
import android.app.AlertDialog
import android.content.Intent

import android.graphics.Color
import android.graphics.drawable.ColorDrawable


import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider


import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.muhasib.snooq.R
import com.muhasib.snooq.mvvm.ShopRegistrationRepository
import com.muhasib.snooq.view.ShopActivity

import com.shuhart.stepview.StepView

class ShopRegistrationActivity : BaseActivity() {



    private lateinit var stepView: StepView
    private lateinit var viewPager: ViewPager2
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private var currentStep = 0
    private lateinit var shopRegistrationViewModel: ShopRegistrationViewModel
    private lateinit var shopRegistrationRepository: ShopRegistrationRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_registration)


        stepView = findViewById(R.id.stepview)
        viewPager = findViewById(R.id.viewPager)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        shopRegistrationViewModel = ViewModelProvider(this)[ShopRegistrationViewModel::class.java]
        shopRegistrationRepository= ShopRegistrationRepository()

        stepView.go(currentStep, true)

        val fragments = listOf(
            BasicInformationFragment(),
            LocationFragment(),
            ShopMediaFragment(),
            PayAndPoliciesFragment()


        )

        viewPager.adapter=object : FragmentStateAdapter(this){
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stepView.go(position,true)
                updateButtons(position, fragments.size)
            }
        })

        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < fragments.size - 1) {
                viewPager.currentItem = currentItem + 1
            } else {
                val payAndPoliciesFragment = supportFragmentManager.findFragmentByTag("f${fragments.size - 1}") as? PayAndPoliciesFragment

                if (payAndPoliciesFragment?.areCheckBoxesChecked() == true) {
                    showConfirmationAlertDialog()
                } else {
                    showCustomSnackbar("Please agree to the Terms and Privacy Policy before proceeding.")
                }
            }
        }

        backButton.setOnClickListener {

            val  currentItem = viewPager.currentItem

            if(currentItem>0){
                viewPager.currentItem=currentItem-1
            }
        }

        updateButtons(0, fragments.size)



    }

    private fun updateButtons(position: Int, total_steps: Int) {

        backButton.visibility = if (position == 0) View.GONE else View.VISIBLE
        nextButton.text = if (position == total_steps - 1) "Submit" else "Next"
    }
    private fun showConfirmationAlertDialog(){

        val dialogView = layoutInflater.inflate(R.layout.submit_shop_details_alert_dialog, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevent dismissing when clicking outside (optional)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCreateShop = dialogView.findViewById<MaterialButton>(R.id.btnCreateShop)
        val btnGoBack = dialogView.findViewById<MaterialButton>(R.id.btnGoBack)

        btnCreateShop.findViewById<Button>(R.id.btnCreateShop).setOnClickListener {

            showProgressDialog()


            val uploadData = UploadData(this, this, shopRegistrationViewModel, shopRegistrationRepository)
            uploadData.uploadData { isSuccess ->
                if (isSuccess) {

                   showCustomSnackbarPositive("Congratulations! Your shop has been successfully created!")


                    startActivity(Intent(this@ShopRegistrationActivity, ShopActivity::class.java))

                } else {
                    showCustomSnackbar("Oops! Failed to create your shop. Please try again.")
                }
                hideProgressDialog()
            }

            dialog.dismiss()
        }

        btnGoBack.findViewById<Button>(R.id.btnGoBack).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}