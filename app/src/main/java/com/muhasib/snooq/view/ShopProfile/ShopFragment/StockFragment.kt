package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.FragmentStockBinding
import com.muhasib.snooq.view.ShopProfile.FloatingDialogFragment

class StockFragment : Fragment() {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)

        // Initialize your view pager adapter (if needed)
        // If you're using a ViewPager2, this is where you'd set it up

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController() // Initialize NavController here
        floatingButton() // Setup the FAB functionality
    }

    private fun floatingButton() {
        binding.customFab.setOnClickListener {
            val token = MySharedPreferences(requireContext()).getToken() // Fix context usage
            Log.d("tokenFab", token.toString())

            if (!token.isNullOrEmpty()) {
                // Pass necessary arguments to FloatingDialogFragment
                val dialog = FloatingDialogFragment(
                    clickChat = { handleChat(it) },
                    clickAudio = { handleChat(it) },
                    clickVideo = { handleChat(it) }
                )
                // Use childFragmentManager for fragment transaction inside the fragment
                dialog.show(childFragmentManager, "FloatingDialogFragment")
            } else {
                Toast.makeText(requireContext(), "Failed to Load, Please Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleChat(chatType: Int) {
        binding.customFab.visibility = View.VISIBLE

        if (navController.currentDestination?.id != R.id.productListingFragment) {
            val bundle = Bundle().apply {
                putString("chatType", chatType.toString()) // Replace "chatType" with your fragment's argument name
            }
            navController.navigate(R.id.productListingFragment, bundle)
        }
    }
}
