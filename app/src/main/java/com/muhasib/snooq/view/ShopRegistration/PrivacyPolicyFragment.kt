package com.muhasib.snooq.view.ShopRegistration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muhasib.snooq.R


class PrivacyPolicyFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val  view= inflater.inflate(R.layout.fragment_privacy_policy, container, false)
        return  view
    }


}