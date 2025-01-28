package com.muhasib.snooq.view.ShopRegistration

import BaseActivity
import ShopRegistrationViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.google.firebase.firestore.FirebaseFirestore
import com.muhasib.snooq.R
import com.muhasib.snooq.constants.userDetail.Companion.SHOP_ID
import com.muhasib.snooq.constants.userDetail.Companion.closedDays
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class LocationFragment : Fragment() {

    private lateinit var locationPickButton: Button
    private lateinit var goToLocationButton: Button
    private lateinit var editTextAddress: TextInputEditText
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var timePickerOpening: TimePicker
    private lateinit var timePickerClosing: TimePicker
    private lateinit var deliverSwitch: Switch
    private lateinit var layoutDeliveryRange: TextInputLayout
    private lateinit var editTextDeliveryRange: TextInputEditText
    private lateinit var chipGroup: ChipGroup

    private lateinit var viewModel: ShopRegistrationViewModel


    companion object {
        const val GPS_REQUEST_CODE = 200
        const val LOCATION_PERMISSION_CODE = 100
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        //Initialize view Model
        viewModel = ViewModelProvider(requireActivity())[ShopRegistrationViewModel::class.java]

        // Bind views
        locationPickButton = view.findViewById(R.id.buttonPickLocation)
        editTextAddress = view.findViewById(R.id.editTextFullAddress)
        goToLocationButton = view.findViewById(R.id.buttonGoToLocation)
        timePickerOpening = view.findViewById(R.id.timePickerOpening)
        timePickerClosing = view.findViewById(R.id.timePickerClosing)
        deliverSwitch = view.findViewById(R.id.switchDelivery)
        chipGroup = view.findViewById(R.id.chipGroupClosedDays)
        layoutDeliveryRange = view.findViewById(R.id.layoutDeliveryRange)
        editTextDeliveryRange = view.findViewById(R.id.editTextDeliveryRange)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationPickButton.setOnClickListener { checkLocationPermission() }

        goToLocationButton.setOnClickListener { moveToGoogleMaps() }

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

        editTextAddress.addTextChangedListener {
            viewModel.updateLocationDetails("fullAddress", it.toString())
        }

        // Set listeners for opening and closing hours
        timePickerOpening.setOnTimeChangedListener { _, hourOfDay, minute ->
            viewModel.updateLocationDetails("openingHour", hourOfDay.toString())
            viewModel.updateLocationDetails("openingMinute", minute.toString())
        }

        timePickerClosing.setOnTimeChangedListener { _, hourOfDay, minute ->
            viewModel.updateLocationDetails("closingHour", hourOfDay.toString())
            viewModel.updateLocationDetails("closingMinute", minute.toString())
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

        action(checkedDaysList)

        return view
    }

    // Chip handling function
    fun action(list: ArrayList<String>) {
        if (list.isEmpty()) {
            Toast.makeText(requireContext(), "No Day selected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "$list", Toast.LENGTH_SHORT).show()
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
                showWebView(addressLine)
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

    private fun showWebView(location: String) {
        val webView1: WebView? = view?.findViewById<WebView>(R.id.web_view)
        if (webView1 == null) {
            Toast.makeText(requireContext(), "WebView not found", Toast.LENGTH_SHORT).show()
            return
        }



        val webView: WebView = view?.findViewById(R.id.web_view) ?: return

        // Enable JavaScript in WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Load Google Maps URL into WebView
        val geoUrl = "https://www.google.com/maps/search/?q=$location"
        webView.loadUrl(geoUrl)
    }
}
