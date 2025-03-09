package com.muhasib.snooq.view.Registration

import com.muhasib.snooq.mvvm.ViewModel.AppWriteViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.muhasib.snooq.R

import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment() {

    private lateinit var username: TextInputEditText
    private lateinit var EmailAddress: TextInputEditText
    private lateinit var btnContinue: Button
    private lateinit var backButtonSignIn: ImageButton

    private val appWriteViewModel : AppWriteViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        // Initialize UI elements
        username = view.findViewById(R.id.usernameTextField)
        EmailAddress = view.findViewById(R.id.mobileTextfield)
        btnContinue = view.findViewById(R.id.continueButton)
        backButtonSignIn = view.findViewById(R.id.backButtonSignIn)
        backButtonSignIn.isEnabled=false


        btnContinue.setOnClickListener {
            val userEmail = EmailAddress.text.toString()
            val userName = username.text.toString()


            if (userEmail.isNotEmpty() && userName.isNotEmpty()) {
                // Call the email verification function
                appWriteViewModel.emailVerification(userEmail)

                // Navigate to OTP Fragment, passing userName and userEmail
                val action = SignInFragmentDirections.actionSignInFragmentToOtpFragment(userName, userEmail)
                findNavController().navigate(action)
            } else {
                // Show a message or error if the fields are empty
                // You can use a Snackbar or Toast to inform the user
                Toast.makeText(context, "Please enter both username and email", Toast.LENGTH_SHORT).show()
            }
        }



        return view
    }

    private fun navigateleft() {
        findNavController().navigate(R.id.action_signInFragment_to_viewpagerfragment)
    }
}
