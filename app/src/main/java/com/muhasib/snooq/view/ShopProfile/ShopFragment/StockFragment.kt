package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.muhasib.snooq.R
import com.muhasib.snooq.adapters.StockAdapter
import com.muhasib.snooq.databinding.FragmentStockBinding

class StockFragment : Fragment() {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)

        // Initialize adapter properly


        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_stockFragment2_to_productListingFragment)
        }




        return binding.root
    }


}
