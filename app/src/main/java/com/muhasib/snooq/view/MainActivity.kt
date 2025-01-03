package com.muhasib.snooq.view

import BaseActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private  lateinit var binding  : ActivityMainBinding
    private lateinit var text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        text =binding.loadProgress
        text.setOnClickListener {

           showCustomSnackbar("Invalid Credentials...")
        }


    }
}