package com.muhasib.snooq.view.onBoarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.muhasib.snooq.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class SecondViewFragment : Fragment() {

    private lateinit var next2: MaterialButton
    private lateinit var viewpager: ViewPager2
    private lateinit var skipBtn2 : TextView
/*
    private lateinit var dotsIndicator: DotsIndicator
*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_second_view, container, false)

        // Initialize views
        next2 = view.findViewById(R.id.Next2ViewPager)
        skipBtn2=view.findViewById(R.id.skip2ViewPager)

        // Ensure fragment is attached before accessing activity
        if (isAdded) {
            viewpager = requireActivity().findViewById(R.id.viewPager)
        } else {
            // Handle the case where fragment is not yet attached
            return view
        }

       /* dotsIndicator = view.findViewById(R.id.dotsIndicator)

        dotsIndicator.attachTo(viewpager)*/

        // Set the click listener
        next2.setOnClickListener {
            viewpager.currentItem = 2 // Navigate to the next page
        }

        skipBtn2.setOnClickListener {
            if (isAdded) {
                findNavController().navigate(R.id.action_viewpagerfragment_to_signInFragment)
            }
        }

        return view
    }
}
