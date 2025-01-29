package com.muhasib.snooq

import BaseActivity
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.muhasib.snooq.R.id
import org.w3c.dom.Text

class TestingLocationActivity : BaseActivity() {

    private lateinit var  openingTimeEditText : EditText
    private lateinit var  closingTimeEditText : EditText
    private lateinit var  openingTimeAm  : TextView
    private lateinit var  closingTimeAm : TextView
    private  lateinit var  btnOpening : Button
    private lateinit var  btnClosing : Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testing_location)

        openingTimeEditText = findViewById(id.openingTimeEdit)
        closingTimeEditText = findViewById(id.closingTimeEdit)
        openingTimeAm = findViewById(id.openingAMTV)
        closingTimeAm = findViewById(id.closingAmTV)
        btnOpening = findViewById(id.btnOpeningTime)
        btnClosing = findViewById(id.btnClosingTime)


        btnOpening.setOnClickListener {
            showTimePicker(isOpeningTime = true)
        }

        btnClosing.setOnClickListener {
            showTimePicker(isOpeningTime = false)
        }


    }
    private fun showTimePicker(isOpeningTime: Boolean) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTitleText(if (isOpeningTime) "Choose Opening Time" else "Choose Closing Time")
            .build()

        timePicker.show(supportFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour == 0 || hour == 12) 12 else hour % 12
            val formattedTime = String.format("%02d:%02d", formattedHour, minute)

            if (isOpeningTime) {
                openingTimeEditText.setText(formattedTime)
                openingTimeAm.text = amPm
            } else {
                closingTimeEditText.setText(formattedTime)
                closingTimeAm.text = amPm
            }
        }
    }
}