package com.muhasib.snooq.view.Registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muhasib.snooq.R

class OtpFragment : Fragment() {
    private lateinit var backButton: ImageButton


    val args: OtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_otp, container, false)

        // Initialize UI components
        backButton = view.findViewById(R.id.backButtonOtp)


        backButton.setOnClickListener {
            navigateLeft()
        }

        // Extract arguments
        val myUsername = args.Username
        val myMobileNumber = args.MobileNumber




        return view
    }

    private fun navigateLeft() {
        findNavController().navigate(R.id.action_otpFragment_to_signInFragment)
    }
}
