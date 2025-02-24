package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.FragmentStockBinding


class StockFragment : Fragment() {

    private  var  _binding : FragmentStockBinding? = null
    private val  binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_stock, container, false)

        _binding= FragmentStockBinding.bind(view)

        binding.btnAddProduct.setOnClickListener {

            findNavController().navigate(R.id.action_stockFragment2_to_productListingFragment)


        }



        return view
    }

}