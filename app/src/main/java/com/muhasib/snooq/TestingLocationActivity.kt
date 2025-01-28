package com.muhasib.snooq

import BaseActivity
import android.app.ProgressDialog
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TestingLocationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_testing_location)

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading Data...")
        progressDialog.setCancelable(false)


        val webView: WebView = findViewById(R.id.web_view)
        webView.requestFocus()
        webView.settings.setLightTouchEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.setGeolocationEnabled(true)

        webView.loadUrl("https://mylocation.org/")


        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, progress: Int) {
                if (progress < 100) {
                    progressDialog.show()
                }
                if (progress == 100) {
                    progressDialog.dismiss()
                }
            }
        }

    }
}