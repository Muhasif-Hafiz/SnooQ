package com.muhasib.snooq.view.Home

import AppWriteViewModel
import BaseActivity
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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

    private lateinit var navigationDrawer: SNavigationDrawer
    private var fragment: Fragment? = null
    private var fragmentClass: Class<*>? = null
    private val appWriteViewModel : AppWriteViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Enable edge-to-edge
        setContentView(R.layout.activity_home)

        window.statusBarColor = resources.getColor(android.R.color.white)

        navigationDrawer = findViewById(R.id.NavDrawer)
       // Designing App Bar Woowhooo
        val typeface = ResourcesCompat.getFont(this, R.font.bold_h2)
        navigationDrawer.setAppbarTitleTV("SnooQ")
        navigationDrawer.setAppbarTitleTypeface(typeface)
        navigationDrawer.pointerIcon
navigationDrawer.menuItemSemiTransparentColor


        // Set up menu items
        val arraylistMenuItems = ArrayList<MenuItem>()
        arraylistMenuItems.add(MenuItem("Home", R.drawable.ic_menu_home))
        arraylistMenuItems.add(MenuItem("Profile", R.drawable.ic_menu_person))
        arraylistMenuItems.add(MenuItem("Create Shop", R.drawable.ic_menu_create_shop))
        arraylistMenuItems.add(MenuItem("Favorite", R.drawable.ic_menu_favorite))
        arraylistMenuItems.add(MenuItem("About Us", R.drawable.ic_menu_about_us))
        arraylistMenuItems.add(MenuItem("Log out", R.drawable.ic_menu_logout))

        navigationDrawer.setMenuItemList(arraylistMenuItems)

        // Default fragment
        fragmentClass = HomeFragment::class.java
        fragment = fragmentClass?.newInstance() as? Fragment

        // Replace fragment
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.frameLayout, it)
                .commit()
        }

        // Set the navigation drawer item click listener
        navigationDrawer.setOnMenuItemClickListener { position ->

            try {
                navigationDrawer.setAppbarTitleTV("") // Set title bar to empty string
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error", Toast.LENGTH_SHORT).show()
            }
            // Handle the menu item click
            fragmentClass = when (position) {
                0 ->  HomeFragment::class.java
                1 -> ProfileFragment::class.java
                2 ->
                {
                    navigateToMyShop()
                    return@setOnMenuItemClickListener
                }
                3 -> FavoriteFragment::class.java
                4 ->

                    {
                    // AboutUsFragment::class.java
                        navigateToTestingActivity()


                    return@setOnMenuItemClickListener

                }
                5 -> {
                    logOutUser()
                    return@setOnMenuItemClickListener
                }
                else -> null
            }

            // Replace the fragment when a menu item is clicked
            fragmentClass?.let {
                fragment = it.newInstance() as Fragment
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .replace(R.id.frameLayout, fragment!!)
                    .commit()
            }

            // Close the drawer
            navigationDrawer.closeDrawer()
        }

        // Drawer listener (if you need additional logic)
        navigationDrawer.setDrawerListener(object : SNavigationDrawer.DrawerListener {


            override fun onDrawerOpening() {

                val colorFrom = Color.parseColor("#FFFFFF") // White
                val colorTo = Color.parseColor("#3AEA5F") // Light Grey
                animateBackgroundColor(colorFrom, colorTo)
            }
            override fun onDrawerClosing() {}
            override fun onDrawerOpened() {

                val colorFrom = Color.parseColor("#F1F1F1") // Light Grey
                val colorTo = Color.parseColor("#FFFFFF") // White
                animateBackgroundColor(colorFrom, colorTo)
            }
            override fun onDrawerClosed() {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
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

            // Dismiss the bottom sheet after clicking logout
            bottomSheetDialog.dismiss()
        }
    }

    private fun animateBackgroundColor(fromColor: Int, toColor: Int) {
        val colorAnimator = ObjectAnimator.ofInt(navigationDrawer, "backgroundColor", fromColor, toColor)
        colorAnimator.setEvaluator(ArgbEvaluator())
        colorAnimator.duration = 500 // Duration of animation
        colorAnimator.start()
    }
    private fun navigateToMyShop(){

        startActivity(Intent(this@HomeActivity, ShopRegistrationActivity::class.java))

    }
    private fun navigateToTestingActivity(){
        startActivity(Intent(this@HomeActivity, TestingLocationActivity::class.java))
    }

}
