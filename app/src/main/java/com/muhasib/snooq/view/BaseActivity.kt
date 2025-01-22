import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract.Colors
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.muhasib.snooq.R
import com.muhasib.snooq.mvvm.appWriteModule
import kotlinx.coroutines.launch


open class BaseActivity : AppCompatActivity() {

    private lateinit var myProgressDialog: Dialog
    private var doubleBackToExitPressedOnce = false
    private lateinit var locationProgressBar: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_progress_bar)




    }
    fun showProgressDialog(){
        myProgressDialog= Dialog(this)

        myProgressDialog.setContentView(R.layout.layout_progress_bar)
        myProgressDialog.setCancelable(false)
        myProgressDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        myProgressDialog.show()


    }



    fun hideProgressDialog(){

        if (::myProgressDialog.isInitialized && myProgressDialog.isShowing) {
            myProgressDialog.dismiss()
        }
    }

    //back button
    fun doubleBackToPress() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            "Please Click again to exit.",
            Toast.LENGTH_SHORT
        ).show()


        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    @SuppressLint("RestrictedApi")
    fun showCustomSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)

        val snackbarView = snackbar.view
        val customView = layoutInflater.inflate(R.layout.custom_snackbar_layout, null)

        snackbarView.setBackgroundColor(Color.TRANSPARENT)

        val layout = snackbarView as Snackbar.SnackbarLayout
        layout.addView(customView, 0)

        val textView = customView.findViewById<TextView>(R.id.snackbar_text)
        textView.text = message

        snackbar.show()
    }
    @SuppressLint("RestrictedApi")
    fun showCustomSnackbarPositive(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)

        val snackbarView = snackbar.view
        val customView = layoutInflater.inflate(R.layout.custom_snackbar_positive, null)

        snackbarView.setBackgroundColor(Color.TRANSPARENT)

        val layout = snackbarView as Snackbar.SnackbarLayout
        layout.addView(customView, 0)

        val textView = customView.findViewById<TextView>(R.id.snackbar_text)
        textView.text = message

        snackbar.show()
    }
    fun showLocationProgressDialog(){

        locationProgressBar = Dialog(this)


        locationProgressBar.setContentView(R.layout.location_progress_bar)
        locationProgressBar.setCancelable(false)
        locationProgressBar.window?.setBackgroundDrawableResource(android.R.color.transparent)

        locationProgressBar.show()


    }

    fun hideLocationProgressbar(){

        if(::locationProgressBar.isInitialized && locationProgressBar.isShowing){

            locationProgressBar.dismiss()
        }
    }


}
