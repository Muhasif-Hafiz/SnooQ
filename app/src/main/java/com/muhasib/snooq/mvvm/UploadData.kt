import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UploadData(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val shopRegistrationViewModel: ShopRegistrationViewModel
) {
    private val db = FirebaseFirestore.getInstance()

    fun uploadData() {
        val (isValid, missingFields) = shopRegistrationViewModel.validateFields()

        if (isValid) {
            val userDetails = shopRegistrationViewModel.userDetailsMap.value ?: hashMapOf()
            val locationDetails = shopRegistrationViewModel.locationDetailsMap.value ?: hashMapOf()
            val paymentDetails = shopRegistrationViewModel.paymentInfoMap.value ?: hashMapOf()

            // Generate Unique IDs for shopkeeper and shop
            val shopkeeperId = userDetails["shopkeeperId"] ?: "shopkeeper_${System.currentTimeMillis()}"
            val shopId = "shop_${System.currentTimeMillis()}"

            val shopDetails = hashMapOf(
                "shop_name" to (userDetails["shopName"] ?: ""),
                "shop_type" to (userDetails["shopCategory"] ?: ""),
                "description" to (userDetails["shopDescription"] ?: ""),
                "address" to (locationDetails["fullAddress"] ?: ""),
                "location" to hashMapOf(
                    "latitude" to (locationDetails["latitude"] ?: 0.0),
                    "longitude" to (locationDetails["longitude"] ?: 0.0)
                ),
                "opening_time" to "${locationDetails["openingHour"] ?: "00"}:${locationDetails["openingMinute"] ?: "00"}",
                "closing_time" to "${locationDetails["closingHour"] ?: "00"}:${locationDetails["closingMinute"] ?: "00"}",
                "days_open" to (locationDetails["selectedDays"] ?: listOf<String>()),
                "current_visitors" to 0,
                "visit_count" to 0,
                "average_rating" to 0.0,
                "rating_count" to 0,
                "offers" to listOf<HashMap<String, Any>>(),
                "products" to listOf<HashMap<String, Any>>(),
                "qr_code" to "",
                "customer_reviews" to listOf<HashMap<String, Any>>(),
                "is_verified" to false,
                "verification_documents" to listOf<String>(),
                "subscription_plan" to "Basic",
                "subscription_expiry" to "",
                "tags" to listOf<String>(),
                "shop_images" to listOf<String>()
            )

            val shopkeeperDetails = hashMapOf(
                "name" to (userDetails["ownerName"] ?: ""),
                "email" to (userDetails["emailAddress"] ?: ""),
                "phone_number" to (userDetails["contactNumber"] ?: ""),
                "profile_picture" to (userDetails["profilePicture"] ?: ""),
                "registration_date" to System.currentTimeMillis(),
                "shops" to hashMapOf(shopId to shopDetails)
            )

            lifecycleOwner.lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        db.collection("shopkeepers").document(shopkeeperId).set(shopkeeperDetails).await()
                        true
                    } catch (e: Exception) {
                        false
                    }
                }

                if (result) {
                    Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val missingFieldsMessage = "The following fields are missing: ${missingFields.joinToString(", ")}"
            Toast.makeText(context, missingFieldsMessage, Toast.LENGTH_LONG).show()
        }
    }
}
