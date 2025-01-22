package com.muhasib.snooq.view.ShopRegistration

import BaseActivity
import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
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
import com.muhasib.snooq.R
import java.io.IOException
import java.util.Locale

class LocationFragment : Fragment() {

    private lateinit var locationPickButton: Button
    private lateinit var goToLocationButton: Button
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var timePickerOpening : TimePicker
    private lateinit var  timePickerClosing : TimePicker
    //switch
    private lateinit var deliverSwitch : Switch
    private lateinit var layoutDeliveryRange: TextInputLayout
    private lateinit var editTextDeliveryRange: TextInputEditText
    // chips
    private lateinit var chipGroup: ChipGroup
    private lateinit var selectedDays: MutableSet<String> // To hold the selected days
    private lateinit var chipSunday: Chip
    private lateinit var chipMonday: Chip
    private lateinit var chipTuesday: Chip
    private lateinit var chipWednesday: Chip
    private lateinit var chipThursday: Chip
    private lateinit var chipFriday: Chip
    private lateinit var chipSaturday: Chip
    private lateinit var  chipAllDay : Chip


    companion object {
        const val GPS_REQUEST_CODE = 200
        const val LOCATION_PERMISSION_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        locationPickButton = view.findViewById(R.id.buttonPickLocation)
        editTextAddress = view.findViewById(R.id.editTextFullAddress)
        goToLocationButton = view.findViewById(R.id.buttonGoToLocation)
        timePickerOpening = view.findViewById(R.id.timePickerOpening)
        timePickerClosing=  view. findViewById(R.id.timePickerClosing)
        deliverSwitch = view.findViewById(R.id.switchDelivery)

        chipSunday = view.findViewById(R.id.chipSunday)
        chipMonday = view.findViewById(R.id.chipMonday)
        chipTuesday = view.findViewById(R.id.chipTuesday)
        chipWednesday = view.findViewById(R.id.chipWednesday)
        chipThursday = view.findViewById(R.id.chipThursday)
        chipFriday = view.findViewById(R.id.chipFriday)
        chipSaturday = view.findViewById(R.id.chipSaturday)
        chipAllDay= view.findViewById(R.id.chipAllDays)
        layoutDeliveryRange =view. findViewById(R.id.layoutDeliveryRange)
        editTextDeliveryRange = view.findViewById(R.id.editTextDeliveryRange)
        
        // setting up the chip listener

        selectedDays = mutableSetOf()
        // Set chip click listeners
        setChipClickListener(chipSunday, "Sunday")
        setChipClickListener(chipMonday, "Monday")
        setChipClickListener(chipTuesday, "Tuesday")
        setChipClickListener(chipWednesday, "Wednesday")
        setChipClickListener(chipThursday, "Thursday")
        setChipClickListener(chipFriday, "Friday")
        setChipClickListener(chipSaturday, "Saturday")
        setChipClickListener(chipAllDay,"All Days")


        // Opening & Closing Hours Functioning
        val openingHour = timePickerOpening.hour
        val openingMinute = timePickerOpening.minute

        val closingHour = timePickerClosing.hour
        val closingMinute = timePickerClosing.minute
        /////////


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationPickButton.setOnClickListener { checkLocationPermission() }

        goToLocationButton.setOnClickListener {moveToGoogleMaps()}
        deliverSwitch.setOnCheckedChangeListener{ _, isChecked ->

            if(isChecked){
                layoutDeliveryRange.visibility = View.VISIBLE
                // Take the Delivery range Data from here ----------------------------------------------
            }else{
                layoutDeliveryRange.visibility = View.GONE
            }



        }





        return view
    }

    private fun setChipClickListener(chip: Chip?, day: String) {
        chip?.setOnClickListener {

            if(selectedDays.contains(day)){
                selectedDays.remove(day)
                chip.setChipBackgroundColorResource(R.color.white)
            }else{
                selectedDays.add(day)
                chip.setChipBackgroundColorResource(R.color.Selected_chip_color)
            }
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

        // Request location updates if last location is null
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            if (location != null) {
                handleLocation(location)
            } else {
                // Fallback: Request location updates if last location is null
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
                val city = address.locality ?: "No City"
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

    private fun moveToGoogleMaps(){

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

}
