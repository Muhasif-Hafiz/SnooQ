package com.muhasib.snooq.view.onBoarding.screens

import android.content.Context
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

class ThirdViewFragment : Fragment() {

   // private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var skipText: TextView
    private lateinit var register: MaterialButton

    companion object {
        private const val ONBOARDING_PREF = "onBoarding"
        private const val FINISH_KEY = "Finish"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_view, container, false)

        // Initialize views
       // dotsIndicator = view.findViewById(R.id.dotsIndicator)
        register = view.findViewById(R.id.StartBtn)
        skipText = view.findViewById(R.id.logintxt)


        if (isAdded) {
            viewPager = requireActivity().findViewById(R.id.viewPager)
        } else {

            return view
        }

       // dotsIndicator.attachTo(viewPager)

        // Handle the register button click
        register.setOnClickListener {
           onBoardingFinished()
            findNavController().navigate(R.id.action_viewpagerfragment_to_signInFragment)
        }

        // Handle the skip button click
        skipText.setOnClickListener {
            onBoardingFinished()
            // Navigates to the home fragment after finishing the onboarding process
            findNavController().navigate(R.id.homeActivity)
        }

        return view
    }

    private fun onBoardingFinished() {
        // Save the state of the onboarding completion using SharedPreferences
        try {
            val sharedPref = requireActivity().getSharedPreferences(ONBOARDING_PREF, Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean(FINISH_KEY, true).apply()
        } catch (e: Exception) {
            e.printStackTrace() // Log the error (optional)
        }
    }
}
