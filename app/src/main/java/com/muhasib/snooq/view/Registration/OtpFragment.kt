package com.muhasib.snooq.view.Registration

import AppWriteViewModel
import BaseActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muhasib.snooq.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpFragment : Fragment() {
    private lateinit var backButton: ImageButton
    private lateinit var submitButton: Button
    private lateinit var otpInputs: List<EditText>

    private val appWriteViewModel: AppWriteViewModel by viewModel()
    private val args: OtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_otp, container, false)

        // Initialize UI components
        backButton = view.findViewById(R.id.backButtonOtp)
        submitButton = view.findViewById(R.id.SubmitButton)

        otpInputs = listOf(
            view.findViewById(R.id.otpDigit1),
            view.findViewById(R.id.otpDigit2),
            view.findViewById(R.id.otpDigit3),
            view.findViewById(R.id.otpDigit4),
            view.findViewById(R.id.otpDigit5),
            view.findViewById(R.id.otpDigit6)
        )

        // Set text watchers for OTP inputs
        otpInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.length == 1) {
                        // Move focus to the next field if current field has a digit
                        if (index < otpInputs.size - 1) {
                            otpInputs[index + 1].requestFocus()
                        }
                    }

                    // Automatically submit if all OTP fields are filled
                    if (otpInputs.all { it.text.length == 1 }) {
                        submitButton.performClick()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // No action needed here
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // If user deletes a digit, move focus to the previous field
                    if (s.isNullOrEmpty() && index > 0) {
                        otpInputs[index - 1].requestFocus()
                    }
                }
            })
        }

        backButton.setOnClickListener { navigateLeft() }
        submitButton.setOnClickListener { handleLogin() }

        return view
    }

    private fun handleLogin() {
        val secretCode = otpInputs.joinToString("") { it.text.toString().trim() }

        if (secretCode.length != 6 || secretCode.any { !it.isDigit() }) {
            (activity as BaseActivity).showCustomSnackbar("Invalid OTP. Please enter a 6-digit code.")
            return
        }

        (activity as BaseActivity).showProgressDialog()

        // Observe the login result
        appWriteViewModel.loginResult.observe(viewLifecycleOwner) { isLoginSuccessful ->
            (activity as BaseActivity).hideProgressDialog()
            if (isLoginSuccessful) {
                navigateToHome()
            } else {
                (activity as BaseActivity).showCustomSnackbar("Login failed. Please check the OTP and try again.")
            }
        }

        // Trigger login
        appWriteViewModel.logginUser(secretCode)
    }

    private fun navigateLeft() {
        findNavController().navigate(R.id.action_otpFragment_to_signInFragment)
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_otpFragment_to_homeFragment22)
    }
}
