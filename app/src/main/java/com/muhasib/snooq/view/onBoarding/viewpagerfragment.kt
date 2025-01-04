package com.muhasib.snooq.view.onBoarding

import FirstViewFragment
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.muhasib.snooq.R

import com.muhasib.snooq.view.onBoarding.screens.SecondViewFragment
import com.muhasib.snooq.view.onBoarding.screens.ThirdViewFragment



class viewpagerfragment : Fragment() {
    private lateinit var viewPager: ViewPager2



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_viewpagerfragment, container, false)
        viewPager = view.findViewById(R.id.viewPager)


        var  fragmentList = arrayListOf<Fragment>(

          FirstViewFragment(),
          SecondViewFragment(),
          ThirdViewFragment()
      )
        var adapter= ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager,  viewLifecycleOwner.lifecycle)
        viewPager.adapter=adapter



        return view
    }


}