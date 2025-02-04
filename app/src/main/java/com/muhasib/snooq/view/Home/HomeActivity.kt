package com.muhasib.snooq.view.Home

import AppWriteViewModel
import BaseActivity
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

import com.muhasib.snooq.R
import com.muhasib.snooq.TestingLocationActivity
import com.muhasib.snooq.view.MainActivity
import com.muhasib.snooq.view.ShopRegistration.ShopRegistrationActivity
import com.shrikanthravi.customnavigationdrawer2.data.MenuItem
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : BaseActivity() {

    private lateinit var  shop_btn :  ImageButton
    private lateinit var   three_dots : ImageButton
    private val appWriteViewModel : AppWriteViewModel by viewModel()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Enable edge-to-edge
        setContentView(R.layout.activity_home)

        shop_btn = findViewById(R.id.shop_icon)
        three_dots=findViewById(R.id.menu_icon)
        shop_btn.setOnClickListener { navigateToShopRegistration() }






        }

    private fun navigateToShopRegistration() {

        startActivity(Intent(this, ShopRegistrationActivity::class.java))
    }


    private fun logOutUser() {


        val bottomSheetDialog = BottomSheetDialog(this)


        val view = layoutInflater.inflate(R.layout.logout_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        // Find the cancel and logout buttons from the inflated layout
        val cancelButton = view.findViewById<MaterialButton>(R.id.cancelButton)
        val logoutButton = view.findViewById<MaterialButton>(R.id.logoutButton)

        // Show the bottom sheet dialog
        bottomSheetDialog.show()

        // Cancel button click listener
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()  // Dismiss the bottom sheet without doing anything
        }

        // Logout button click listener
        logoutButton.setOnClickListener {
            // Call the logout function here
            appWriteViewModel.LogoutUserSession()

            // Observe the logout result from the ViewModel
            appWriteViewModel.logoutResult.observe(this, Observer { result ->
                result.fold(
                    onSuccess = {
                        // If successful, navigate to MainActivity and finish the current activity
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // Finish the current activity to prevent returning to it
                    },
                    onFailure = { exception ->
                        // Show an error message in case of failure
                        showCustomSnackbar("Please Check Your Network Connection!")
                    }
                )
            })


            bottomSheetDialog.dismiss()
        }
    }


}
