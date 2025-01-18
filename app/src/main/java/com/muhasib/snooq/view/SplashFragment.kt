package com.muhasib.snooq.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhasib.snooq.R
import com.muhasib.snooq.mvvm.AppWriteRepository
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

        //
        val client: Client = AppWriteSingleton.getClient(requireContext())
        val account = Account(client)


        Handler().postDelayed({

            if(onBoardingFinish()){
                viewLifecycleOwner.lifecycleScope.launch {
                    try {

                        val user = account.get()

                        if (user != null) {
                            findNavController().navigate(R.id.action_splashFragment_to_homeFragment22)
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                        }

                    }catch (e: AppwriteException) {
                        // If an error occurs (user not logged in), navigate to SignInFragment
                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                    }
                }

            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewpagerfragment)
            }

        },3000)

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun  onBoardingFinish() : Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)

        return sharedPref.getBoolean("Finish", false)
    }




}