package com.muhasib.snooq.view.ShopProfile

import BaseActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.muhasib.snooq.R
import com.muhasib.snooq.databinding.ActivityShopBinding

class ShopActivity : BaseActivity() {

    private lateinit var binding: ActivityShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
