package com.muhasib.snooq.view.ShopProfile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.muhasib.snooq.R

class ShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, ShopkeeperProfileFragment())
//                .commit()
//        }
    }
}
