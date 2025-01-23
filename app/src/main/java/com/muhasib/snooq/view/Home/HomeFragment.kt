package com.muhasib.snooq.view.Home

import BaseActivity
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.muhasib.snooq.R
import com.shrikanthravi.customnavigationdrawer2.widget.SNavigationDrawer



class HomeFragment : Fragment() {


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_home, container, false)


        val imageView = view.findViewById<ImageView>(R.id.imageView)
        val url = "https://cloud.appwrite.io/v1/storage/buckets/6792800d001d344a8d58/files/6792855d001b9eadfb33/view?project=677a4b92001bbd3a3742&project=677a4b92001bbd3a3742&mode=admin"

        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.placeholder_image)  // Optional placeholder
            .error(R.drawable.error_image)  // Optional error image
            .into(imageView)

        return view

    }


}