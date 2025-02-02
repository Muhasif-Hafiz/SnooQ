package com.muhasib.snooq.view.ShopRegistration

import BaseActivity
import ShopRegistrationViewModel
import android.Manifest
import android.annotation.SuppressLint

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.os.Looper

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.muhasib.snooq.R
import com.muhasib.snooq.model.NominatimResponse
import com.muhasib.snooq.singleton.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Locale

class LocationFragment : Fragment() {

    private lateinit var locationSubmitButton: Button
    private lateinit var locationImageView: ImageView
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var deliverSwitch: SwitchCompat
    private lateinit var layoutDeliveryRange: TextInputLayout
    private lateinit var editTextDeliveryRange: TextInputEditText
    private lateinit var chipGroup: ChipGroup

    private lateinit var  openingTimeEditText : EditText
    private lateinit var  closingTimeEditText : EditText
    private lateinit var  openingTimeAm  : TextView
    private lateinit var  closingTimeAm : TextView
    private  lateinit var  btnOpening : Button
    private lateinit var  btnClosing : Button

    private lateinit var viewModel: ShopRegistrationViewModel


    companion object {
        const val GPS_REQUEST_CODE = 200
        const val LOCATION_PERMISSION_CODE = 100
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)


        viewModel = ViewModelProvider(requireActivity())[ShopRegistrationViewModel::class.java]


        val scrollView: ScrollView = view.findViewById(R.id.scrollView)
        scrollView.setEdgeEffectColor(Color.GREEN)
        scrollView.isSmoothScrollingEnabled
        // Bind views
        locationSubmitButton = view.findViewById(R.id.btnSubmitLocation)
        editTextAddress = view.findViewById(R.id.editCity)
        locationImageView = view.findViewById(R.id.locationImageView)

        deliverSwitch = view.findViewById(R.id.switchDelivery)
        chipGroup = view.findViewById(R.id.chipGroupClosedDays)
        layoutDeliveryRange = view.findViewById(R.id.layoutDeliveryRange)
        editTextDeliveryRange = view.findViewById(R.id.editTextDeliveryRange)

        // time picker
        openingTimeEditText = view.findViewById(R.id.openingTimeEdit)
        closingTimeEditText = view.findViewById(R.id.closingTimeEdit)
        openingTimeAm = view.findViewById(R.id.openingAMTV)
        closingTimeAm = view.findViewById(R.id.closingAmTV)
        btnOpening = view.findViewById(R.id.btnOpeningTime)
        btnClosing = view.findViewById(R.id.btnClosingTime)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())



        locationSubmitButton.setOnClickListener {

            val location = editTextAddress.text.toString()

            (activity as BaseActivity).showLocationProgressDialog()
            getLatLngFromAddress(location)

        }
        locationImageView.setOnClickListener { moveToGoogleMaps() }











        deliverSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                layoutDeliveryRange.visibility = View.VISIBLE
                viewModel.updateLocationDetails("DeliveryAvailable", "true")
                editTextDeliveryRange.addTextChangedListener {
                    viewModel.updateLocationDetails("DeliveryRadius", it.toString())
                }
            } else {
                layoutDeliveryRange.visibility = View.GONE
                viewModel.updateLocationDetails("DeliveryAvailable", "false")
            }
        }


        val checkedDaysList = arrayListOf<String>()
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            checkedDaysList.clear()
            if (checkedIds.isNotEmpty()) {
                checkedIds.forEach { idx ->
                    val chip = view.findViewById<Chip>(idx)
                    checkedDaysList.add(chip.text.toString())
                }
                viewModel.updateLocationDetails("selectedDays", checkedDaysList.toString())
            }
        }

        btnOpening.setOnClickListener {
            showTimePickerOpening()
        }

        btnClosing.setOnClickListener {
            showTimePickerClosing()
        }

        return view
    }
    @SuppressLint("SuspiciousIndentation")
    private fun showTimePickerOpening() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setTheme(R.style.CustomTimePickerTheme)
            .setMinute(0)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTitleText( "Choose Opening Time" )
            .build()


        timePicker.show(parentFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour == 0 || hour == 12) 12 else hour % 12
            val formattedTime = String.format("%02d:%02d", formattedHour, minute)

                openingTimeEditText.setText(formattedTime)
                openingTimeAm.text = amPm


            viewModel.updateLocationDetails("openingTime", openingTimeEditText.text.toString() )





        }
    }

    private fun showTimePickerClosing() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)

            .setTheme(R.style.CustomTimePickerTheme)
            .setTitleText( "Choose Closing Time" )
            .build()


        timePicker.show(parentFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour == 0 || hour == 12) 12 else hour % 12
            val formattedTime = String.format("%02d:%02d", formattedHour, minute)

            closingTimeEditText.setText(formattedTime)
             closingTimeAm.text = amPm

       viewModel.updateLocationDetails("closingTime", closingTimeEditText.text.toString() )





        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkGPS()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun checkGPS() {
        locationRequest = LocationRequest.Builder(
            LocationRequest.PRIORITY_HIGH_ACCURACY, 1000L
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000L)
            .setMaxUpdateDelayMillis(10000L)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                getUserLocation()
            } catch (e: ApiException) {
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            val resolvable = e as ResolvableApiException
                            resolvable.startResolutionForResult(requireActivity(), GPS_REQUEST_CODE)
                        } catch (sendIntentException: IntentSender.SendIntentException) {
                            sendIntentException.printStackTrace()
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Toast.makeText(
                            requireContext(),
                            "GPS settings unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
            return
        }
        (activity as BaseActivity).showLocationProgressDialog()

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            if (location != null) {
                handleLocation(location)
            } else {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        if (locationResult != null) {
                            super.onLocationResult(locationResult)
                        }
                        val currentLocation = locationResult?.lastLocation
                        if (currentLocation != null) {
                            handleLocation(currentLocation)
                        }
                    }
                }, Looper.getMainLooper())
            }
        }
    }

    private fun handleLocation(location: Location?) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses =
                location?.let { geocoder.getFromLocation(it.latitude, location.longitude, 1) }

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0) ?: "No Address"

                val country = address.countryName ?: "India"
                val finalLocation = "$addressLine, $country"
                editTextAddress.setText(finalLocation)



            } else {
                Toast.makeText(requireContext(), "No address found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error fetching address", Toast.LENGTH_SHORT).show()
        }
        (activity as BaseActivity).hideLocationProgressbar()
    }

    private fun moveToGoogleMaps() {
        if (!editTextAddress.text.isNullOrEmpty()) {
            val location = editTextAddress.text.toString()


            val uri = Uri.parse("geo:0,0?q=$location")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Address is empty", Toast.LENGTH_SHORT).show()
        }
    }


    fun getLatLngFromAddress(address: String) {
        val call = RetrofitClient.instance.getLocation(address)

        call.enqueue(object : Callback<List<NominatimResponse>> {
            override fun onResponse(call: Call<List<NominatimResponse>>, response: Response<List<NominatimResponse>>) {
                if (response.isSuccessful) {
                    val locations = response.body()

                    if (!locations.isNullOrEmpty()) {
                        val location = locations.first()
                        val latitude = location.latitude.toDoubleOrNull() ?: 0.0
                        val longitude = location.longitude.toDoubleOrNull() ?: 0.0

                        // Load static map with location
                        loadStaticMap(latitude, longitude, 10, locationImageView)

                        // Update UI elements
                        editTextAddress.setText(location.displayName)
                        viewModel.updateLocationDetails("fullAddress", location.displayName)
                    } else {
                        editTextAddress.text?.clear()

                        Toast.makeText(requireContext(), "Couldn't find such city", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid city name", Toast.LENGTH_SHORT).show()
                    Log.e("Geocoding", "Error: ${response.errorBody()?.string()}")
                }
                (activity as BaseActivity).hideLocationProgressbar()
            }

            override fun onFailure(call: Call<List<NominatimResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                (activity as BaseActivity).hideLocationProgressbar()
                Log.e("Geocoding", "Failure: ${t.message}", t)
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

        (activity as BaseActivity).hideLocationProgressbar()
    }
}







