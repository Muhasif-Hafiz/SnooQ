package com.muhasib.snooq.view.Home

import com.muhasib.snooq.mvvm.ViewModel.AppWriteViewModel
import BaseActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton

import com.muhasib.snooq.R
import com.muhasib.snooq.view.MainActivity
import com.muhasib.snooq.view.ShopProfile.ShopActivity
import com.muhasib.snooq.view.ShopRegistration.ShopRegistrationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : BaseActivity() {

//    private lateinit var  shop_btn :  ImageButton
//    private lateinit var   three_dots : ImageButton
//    private val appWriteViewModel : AppWriteViewModel by viewModel()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

//        shop_btn = findViewById(R.id.shop_icon)
//        three_dots=findViewById(R.id.menu_icon)
//        shop_btn.setOnClickListener { navigateToShopRegistration() }
//
//
//        val btn : MaterialButton= findViewById(R.id.ShopActivity)
//        btn.setOnClickListener{
//            startActivity(Intent(this@HomeActivity, ShopActivity::class.java))
//        }


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


        bottomSheetDialog.show()
        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Logout button click listener
//        logoutButton.setOnClickListener {
//
//            appWriteViewModel.LogoutUserSession()
//
//
//            appWriteViewModel.logoutResult.observe(this, Observer { result ->
//                result.fold(
//                    onSuccess = {
//
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                    },
//                    onFailure = { exception ->
//                        showCustomSnackbar("Please Check Your Network Connection!")
//                    }
//                )
//            })
//
//
//            bottomSheetDialog.dismiss()
//        }
    }


}
