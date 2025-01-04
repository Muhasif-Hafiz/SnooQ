package com.muhasib.snooq.view.onBoarding.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.muhasib.snooq.R
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class ThirdViewFragment : Fragment() {

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewpager: ViewPager2
    private lateinit var skipText : TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_third_view, container, false)
        dotsIndicator = view.findViewById(R.id.dotsIndicator)
        viewpager = requireActivity().findViewById(R.id.viewPager)
        skipText= view.findViewById(R.id.skipText)
        dotsIndicator.attachTo(viewpager)


        val  reciever=

        skipText.setOnClickListener {


            onBoardingFinished()
            findNavController().navigate(R.id.action_viewpagerfragment_to_homeFragment22)

        }

        return view
    }
private fun onBoardingFinished(){

    val sharedPref= requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
    val editor= sharedPref.edit()
    editor.putBoolean("Finish", true)
    editor.apply()
}



}