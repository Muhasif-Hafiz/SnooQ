package com.muhasib.snooq.view.Home

import BaseActivity
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EdgeEffect
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.muhasib.snooq.R
import com.muhasib.snooq.adapters.DashboardAdapter
import com.muhasib.snooq.databinding.FragmentHomeBinding
import com.muhasib.snooq.model.DashboardItem
import com.muhasib.snooq.mvvm.ViewModel.AppWriteViewModel
import com.muhasib.snooq.view.MainActivity
import com.muhasib.snooq.view.ShopProfile.ShopActivity
import com.muhasib.snooq.view.ShopRegistration.ShopRegistrationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val appWriteViewModel: AppWriteViewModel by viewModel()

    private val dashboardAdapter by lazy {
        DashboardAdapter { item ->
            onDashboardItemClick(item)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val recyclerView = binding.rvDashboard

        recyclerView.edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return EdgeEffect(view.context).apply {
                    color = ContextCompat.getColor(view.context, R.color.PrimarySnooQ)
                }
            }
        }

        setupGreeting()
        setupRecyclerView()
        loadDashboardItems()
        setupToggleSwitch()
        binding.shopIcon.setOnClickListener{navigateToShopRegistration()}

        return binding.root
    }


    private fun navigateToShopRegistration() {
        startActivity(Intent(requireContext(), ShopRegistrationActivity::class.java))
    }

    private fun logOutUser() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.logout_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        val cancelButton = view.findViewById<MaterialButton>(R.id.cancelButton)
        val logoutButton = view.findViewById<MaterialButton>(R.id.logoutButton)

        bottomSheetDialog.show()

        cancelButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        logoutButton.setOnClickListener {
            appWriteViewModel.LogoutUserSession()

            appWriteViewModel.logoutResult.observe(viewLifecycleOwner,  { result ->
                result.fold(
                    onSuccess = {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    },
                    onFailure = {

                        (activity as BaseActivity).showCustomSnackbar("Please Check Your Network Connection!")
                    }
                )
            })

            bottomSheetDialog.dismiss()
        }
    }

    private fun setupGreeting() {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (currentHour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }

        binding.tvTitle.text = "$greeting,"
        binding.tvSubtitle.text = "Muhasib Hafeez"
    }


    private fun setupRecyclerView() {
        binding.rvDashboard.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = dashboardAdapter
        }
    }

    private fun loadDashboardItems() {

        val dashboardItems = listOf(
            DashboardItem(1, "My Profile ", R.drawable.ic_profile),
            DashboardItem(2, "Catalog", R.drawable.ic_catalog),
            DashboardItem(3, "Community", R.drawable.ic_community),
            DashboardItem(4, "Manage Orders", R.drawable.ic_orders),
            DashboardItem(5, "Discounts", R.drawable.ic_discounts),
            DashboardItem(6, "Settings", R.drawable.ic_settings)
        )

        dashboardAdapter.submitList(dashboardItems)
    }


    private fun onDashboardItemClick(item: DashboardItem) {
        Toast.makeText(requireContext(), "${item.title} clicked", Toast.LENGTH_SHORT).show()
        when (item.id) {
            1 -> {
                navigateToMyProfile()
            }

            2 -> { /* Navigate to Scholarships */
            }

            3 -> { /* Navigate to Repayments */
            }

            4 -> { /* Navigate to Clearance */
            }

            5 -> { /* Navigate to Refunds */
            }

            6 -> { /* Navigate to Contact us */
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun navigateToMyProfile(){
        startActivity(Intent(requireContext(), ShopActivity::class.java))
    }
    // Add this to your Activity or Fragment
    private fun setupToggleSwitch() {
        val toggleBackground =binding.toggleBackground

        val toggleButton = binding.toggleButton
        val toggleIndicator =binding.toggleIndicator
        var isToggleOn = false

        toggleBackground.setOnClickListener {
            isToggleOn = !isToggleOn

            // Animate toggle button position
            val translationX = if (isToggleOn) (toggleBackground.width - toggleButton.width - 1).toFloat() else 1f
            toggleButton.animate()
                .translationX(translationX)
                .setDuration(200)
                .start()

            // Animate background color change
            val colorFrom = if (isToggleOn) Color.parseColor("#EAEAEA") else Color.parseColor("#D6EFDB")
            val colorTo = if (isToggleOn) Color.parseColor("#D6EFDB") else Color.parseColor("#EAEAEA")

            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 200 // milliseconds
            colorAnimation.addUpdateListener { animator ->
                toggleBackground.setCardBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimation.start()

            // Change indicator alpha
            toggleIndicator.animate()
                .alpha(if (isToggleOn) 1.0f else 0.5f)
                .setDuration(200)
                .start()

            // Perform action based on toggle state
            // Your toggle action code here
        }
    }

}
