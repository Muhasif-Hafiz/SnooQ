package com.muhasib.snooq.view.ShopRegistration

import BaseActivity


import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.muhasib.snooq.R

import com.shuhart.stepview.StepView

class ShopRegistrationActivity : BaseActivity() {



    private lateinit var stepView: StepView
    private lateinit var viewPager: ViewPager2
    private lateinit var nextButton: MaterialButton
    private lateinit var backButton: MaterialButton
    private var currentStep = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_shop_registration)


        stepView = findViewById(R.id.stepview)
        viewPager = findViewById(R.id.viewPager)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)

        stepView.go(currentStep, true)

        val fragments = listOf(
            BasicInformationFragment(),
            LocationFragment(),
            ShopMediaFragment(),
            PayAndPoliciesFragment()


        )

        viewPager.adapter=object : FragmentStateAdapter(this){
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stepView.go(position,true)
                updateButtons(position, fragments.size)
            }
        })

        nextButton.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < fragments.size - 1) {
                viewPager.currentItem = currentItem + 1
            } else {

                Toast.makeText(this@ShopRegistrationActivity, " Shop Registered", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {

            val  currentItem = viewPager.currentItem

            if(currentItem>0){
                viewPager.currentItem=currentItem-1
            }
        }

        updateButtons(0, fragments.size)
    }

    private fun updateButtons(position: Int, total_steps: Int) {

        backButton.visibility = if (position == 0) View.GONE else View.VISIBLE
        nextButton.text = if (position == total_steps - 1) "Submit" else "Next"
    }

}