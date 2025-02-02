package com.muhasib.snooq

import BaseActivity
import android.annotation.SuppressLint

import android.os.Bundle
import android.util.Log

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

import com.muhasib.snooq.model.NominatimResponse
import com.muhasib.snooq.singleton.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestingLocationActivity : BaseActivity() {


    private lateinit var editText : EditText
    private lateinit var  btnSearch : MaterialButton
    private lateinit var  locationText : TextView
    private lateinit var  imgLocation : ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testing_location)



        editText= findViewById(R.id.etLocation)
        btnSearch= findViewById(R.id.btnSearchLocation)
        locationText = findViewById(R.id.locationText)
        imgLocation = findViewById(R.id.mapImageView)



        btnSearch.setOnClickListener{

            val location = editText.text.toString()
            getLatLngFromAddress(location)
        }




    }
    fun getLatLngFromAddress(address: String) {
        val call = RetrofitClient.instance.getLocation(address)

        call.enqueue(object : Callback<List<NominatimResponse>> {
            override fun onResponse(call: Call<List<NominatimResponse>>, response: Response<List<NominatimResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.firstOrNull()?.let { location ->

                        val latitude = location.latitude.toDoubleOrNull() ?: 0.0
                        val longitude = location.longitude.toDoubleOrNull() ?: 0.0

                        // Load static map with location
                        loadStaticMap(latitude, longitude, 10, imgLocation)

                        // Display location information
                        locationText.text = "${location.displayName} ($latitude, $longitude)"
                    } ?: Log.e("Geocoding", "No results found")
                } else {
                    Log.e("Geocoding", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<NominatimResponse>>, t: Throwable) {
                Log.e("Geocoding", "Request failed: ${t.message}")
            }
        })
    }


    fun loadStaticMap(latitude: Double, longitude: Double, zoom: Int, imageView: ImageView) {
        // Construct the static map URL with lang set to English
        val staticMapUrl = "https://static-maps.yandex.ru/1.x/?ll=$longitude,$latitude&z=$zoom&size=650,450&l=map&pt=$longitude,$latitude,pm2rdm&lang=en"

        // Load the map image into the ImageView using Glide
        Glide.with(imageView.context)
            .load(staticMapUrl)
            .into(imageView)
    }
}
