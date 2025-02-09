package com.muhasib.snooq.view.ShopProfile

import BaseActivity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore

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
        val shop_id= getShopId(this)
        Toast.makeText(this, shop_id, Toast.LENGTH_SHORT).show()


        loadUserData(shop_id)

    }

    private fun loadUserData(shop_id : String) {
        val db = FirebaseFirestore.getInstance()

        Log.d("Firestore", "Fetching data for SHOP_ID: $shop_id")

        db.collection("shops").document(shop_id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("Firestore", "Document data: ${document.data}")

                    val shopkeeperData = document.data?.values?.firstOrNull() as? Map<*, *>
                    val shopkeeperName = shopkeeperData?.get("name") as? String ?: "N/A"
                    val shopkeeperEmail = shopkeeperData?.get("email") as? String ?: "N/A"
                    val shopsList = shopkeeperData?.get("shops") as? List<Map<*, *>>

                    if (shopsList != null && shopsList.isNotEmpty()) {
                        val shopData = shopsList[0]  // Get first shop

                        val shopName = shopData["shopName"] as? String ?: "N/A"

                        val shopDescription = shopData["description"] as? String ?: "N/A"
                        val address = shopData["address"] as? String ?: "N/A"

                        val location = shopData["location"] as? Map<*, *>
                        val openingTime = location?.get("openingTime") as? String ?: "N/A"
                        val closingTime = location?.get("closingTime") as? String ?: "N/A"

                        runOnUiThread {
                            binding.shopNameShopActivity.text = shopName
                            binding.shopOwnerNameShopActivity.text = shopkeeperName
                            binding.shopDescriptionShopActivity.text = shopDescription
                            binding.shopAddressShopActivity.text = address
                            binding.textOpenTimeShopActivity.text = openingTime
                            binding.closingTimeShopActivity.text = closingTime
                        }
                    } else {
                        Log.e("Firestore", "No shop data found")
                    }
                } else {
                    Log.d("Firestore", "Document does not exist.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching shop data", exception)
            }
    }
    fun getShopId(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val id= sharedPreferences.getString("SHOP_ID", null) //
    return id.toString()// Return null if not found
    }


}
