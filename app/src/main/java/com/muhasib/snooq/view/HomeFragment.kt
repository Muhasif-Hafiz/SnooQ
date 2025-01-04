package com.muhasib.snooq.view

import BaseActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.muhasib.snooq.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class HomeFragment : Fragment() {
    private lateinit var text : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        val baseActivity = activity as? BaseActivity
        text=view.findViewById(R.id.HomeTesting)
        text.setOnClickListener {

            baseActivity?.showCustomSnackbar("Testing SnackBar... Thanks Yasar")

                    }

        return view

    }


}