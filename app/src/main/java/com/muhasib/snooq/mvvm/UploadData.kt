import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.muhasib.snooq.constants.userDetail.Companion.closedDays
import com.muhasib.snooq.mvvm.ShopRegistrationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadData(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val shopRegistrationViewModel: ShopRegistrationViewModel,
    private val shopRegistrationRepository: ShopRegistrationRepository
) {

    fun uploadData() {
        val (isValid, missingFields) = shopRegistrationViewModel.validateFields()

        if (isValid) {
            val userDetails = shopRegistrationViewModel.userDetailsMap.value ?: hashMapOf()
            val locationDetails = shopRegistrationViewModel.locationDetailsMap.value ?: hashMapOf()
            val paymentDetails = shopRegistrationViewModel.paymentInfoMap.value ?: hashMapOf()

            val shopDetails = hashMapOf<String, Any>().apply {
                put("shopName", userDetails["shopName"] ?: "")
                put("shopCategory", userDetails["shopCategory"] ?: "")
                put("ownerName", userDetails["ownerName"] ?: "")
                put("contactNumber", userDetails["contactNumber"] ?: "")
                put("emailAddress", userDetails["emailAddress"] ?: "")
                put("shopDescription", userDetails["shopDescription"] ?: "")
                put("fullAddress", locationDetails["fullAddress"] ?: "")
                put("openingHour", locationDetails["openingHour"] ?: "")
                put("closingHour", locationDetails["closingHour"] ?: "")
                put("closingMinute", locationDetails["closingMinute"] ?: "")
                put("openingMinute", locationDetails["openingMinute"] ?: "")
                put("openingMinute", locationDetails["openingMinute"] ?: "")
                put("selectedDays", locationDetails["selectedDays"] ?: "")

                put("DeliveryAvailable", locationDetails["DeliveryAvailable"] ?: "false")
                put("DeliveryRadius", locationDetails["DeliveryRadius"] ?: "0")
                put("bankName", paymentDetails["bankName"] ?: "")
                put("accountNumber", paymentDetails["accountNumber"] ?: "")
                put("ifscCode", paymentDetails["ifscCode"] ?: "")
                put("refundPolicy", paymentDetails["refundPolicy"] ?: "")
            }

            lifecycleOwner.lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    shopRegistrationRepository.uploadShopDetails(shopDetails)
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
