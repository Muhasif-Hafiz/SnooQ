package com.muhasib.snooq.view

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhasib.snooq.R
import com.muhasib.snooq.singleton.AppWriteSingleton
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        val client: Client = AppWriteSingleton.getClient(requireContext())
        val account = Account(client)

        if (isInternetAvailable(requireContext())) {
            proceedWithAppFlow(account)
        } else {
            showNoInternetDialog()
        }

        return view
    }

    private fun proceedWithAppFlow(account: Account) {
        if (onBoardingFinished()) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val user = account.get()
                    if (user != null) {
                        findNavController().navigate(R.id.homeActivity)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                    }
                } catch (e: AppwriteException) {
                    findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                }
            }
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_viewpagerfragment)
        }
    }

    private fun showNoInternetDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_no_internet, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)

        val buttonRetry = dialogView.findViewById<Button>(R.id.buttonRetry)
        val buttonExit = dialogView.findViewById<Button>(R.id.buttonExit)

        buttonRetry.setOnClickListener {
            if (isInternetAvailable(requireContext())) {
                alertDialog.dismiss()
                proceedWithAppFlow(Account(AppWriteSingleton.getClient(requireContext())))
            } else {
                Toast.makeText(requireContext(), "No internet connection. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonExit.setOnClickListener {
            requireActivity().finish()
        }

        alertDialog.show()
    }


    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finish", false)
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
