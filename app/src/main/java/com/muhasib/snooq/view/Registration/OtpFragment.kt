package com.muhasib.snooq.view.Registration

import com.muhasib.snooq.mvvm.ViewModel.AppWriteViewModel
import BaseActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muhasib.snooq.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpFragment : Fragment() {
    private lateinit var backButton: ImageButton
    private lateinit var submitButton: Button
    private lateinit var otpInputs: List<EditText>
    private lateinit var timerTextView: TextView
    private lateinit var resendButton: TextView
    private var countDownTimer: CountDownTimer? = null
    private lateinit  var progress : ProgressBar
    private lateinit var progress_layout : LinearLayout

    private val appWriteViewModel: AppWriteViewModel by viewModel()
    private val args: OtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_otp, container, false)

        // Initialize UI components
        backButton = view.findViewById(R.id.backButtonOtp)
        submitButton = view.findViewById(R.id.submit_button)
        timerTextView = view.findViewById(R.id.timerText)
        resendButton = view.findViewById(R.id.resendButton)
        progress = view.findViewById(R.id.progress_bar)
        progress_layout = view.findViewById(R.id.progress_layout)


        otpInputs = listOf(
            view.findViewById(R.id.otpDigit1),
            view.findViewById(R.id.otpDigit2),
            view.findViewById(R.id.otpDigit3),
            view.findViewById(R.id.otpDigit4),
            view.findViewById(R.id.otpDigit5),
            view.findViewById(R.id.otpDigit6)
        )

        var email = args.MobileNumber.toString()


        otpInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.length == 1) {
                        if (index < otpInputs.size - 1) {
                            otpInputs[index + 1].requestFocus()
                        }
                    }

                    if (otpInputs.all { it.text.length == 1 }) {
                        submitButton.performClick()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty() && index > 0) {
                        otpInputs[index - 1].requestFocus()
                    }
                }
            })
        }

        // Set up listeners for buttons
        backButton.setOnClickListener { navigateLeft() }
        submitButton.setOnClickListener { handleLogin() }
        resendButton.setOnClickListener { resendOtp(email) }

        // Start the timer
        startOtpTimer()

        return view
    }

    private fun startOtpTimer() {
        // Cancel any existing timer before starting a new one
        countDownTimer?.cancel()

        // Set the timer for 2 minutes (120,000 milliseconds)
        countDownTimer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer text
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = (millisUntilFinished / 1000) / 60
                timerTextView.text = String.format("Wait: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // Show a message when the timer finishes

                resendButton.visibility = View.VISIBLE // Enable the resend button
                submitButton.isEnabled = false // Disable the submit button
            }
        }.start()
    }

    private fun resendOtp(email : String) {


        appWriteViewModel.emailVerification(email)

        (activity as BaseActivity).showCustomSnackbarPositive("OTP resent successfully!")

        // Restart the timer
        startOtpTimer()

        // Disable the resend button until the timer finishes
        resendButton.isEnabled = false
        submitButton.isEnabled = true
    }

    private fun handleLogin() {
        val secretCode = otpInputs.joinToString(separator = "") { it.text.toString().trim() }



        if (secretCode.length != 6 || secretCode.any { !it.isDigit() }) {
            (activity as BaseActivity).showCustomSnackbar("Invalid OTP. Please enter a 6-digit code.")
            return
        }

        // Show progress bar and disable the submit button
        progress.visibility = View.VISIBLE
        submitButton.text = "Verifying..."
        submitButton.isEnabled = false

        (activity as BaseActivity).showProgressDialog()

        appWriteViewModel.loginResult.observe(viewLifecycleOwner) { isLoginSuccessful ->
            (activity as BaseActivity).hideProgressDialog()
            progress.visibility = View.GONE  // Hide the progress bar
            submitButton.isEnabled = true // Re-enable the submit button
            submitButton.text = "Submit" // Reset the button text

            if (isLoginSuccessful) {
                navigateToHome()
            } else {
                (activity as BaseActivity).showCustomSnackbar("Login failed. Please check the OTP and try again.")
            }
        }

        appWriteViewModel.logginUser(secretCode)
    }



    private fun navigateLeft() {
        findNavController().navigate(R.id.action_otpFragment_to_signInFragment)
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.homeActivity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the timer when the view is destroyed
        countDownTimer?.cancel()
    }



}
