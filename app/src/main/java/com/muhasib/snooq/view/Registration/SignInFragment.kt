package com.muhasib.snooq.view.Registration

import BaseActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.muhasib.snooq.R
import com.muhasib.snooq.R.id.mobileTextfield

class SignInFragment : Fragment() {

    private lateinit var username: TextInputEditText
    private lateinit var mobileNumber: TextInputEditText
    private lateinit var btnContinue: Button
    private lateinit var backButtonSignIn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)



        username = view.findViewById(R.id.usernameTextField)
        mobileNumber = view.findViewById(R.id.mobileTextfield)
        btnContinue = view.findViewById(R.id.continueButton)
        backButtonSignIn = view.findViewById(R.id.backButtonSignIn)

        // Set onClickListener for the continue button
        btnContinue.setOnClickListener {
            val userName = username.text.toString()
            val mobileNumber = mobileNumber.text.toString()

            val action = SignInFragmentDirections.actionSignInFragmentToOtpFragment(userName, mobileNumber)
            findNavController().navigate(action)
        }
        backButtonSignIn.setOnClickListener {
            navigateleft()
        }

        // back press manually functionality



        return view
    }
    private fun navigateleft() {
        findNavController().navigate(R.id.action_signInFragment_to_viewpagerfragment)
    }

}
