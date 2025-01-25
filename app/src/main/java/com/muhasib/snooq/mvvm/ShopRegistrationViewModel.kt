import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShopRegistrationViewModel : ViewModel() {

    // HashMaps for different fragments
    val userDetailsMap = MutableLiveData<HashMap<String, String>>(hashMapOf())
    val locationDetailsMap = MutableLiveData<HashMap<String, String>>(hashMapOf())
    val paymentInfoMap = MutableLiveData<HashMap<String, String>>(hashMapOf())
    val mediaFiles = MutableLiveData<List<Uri>>(mutableListOf())

    private val _selectedDays = MutableLiveData<Set<String>>()
    val selectedDays: LiveData<Set<String>> get() = _selectedDays

    fun updateSelectedDays(newSelectedDays: Set<String>) {
        _selectedDays.value = newSelectedDays
    }

    // Method to validate fields by checking if any required fields are missing
    fun validateFields(): Pair<Boolean, List<String>> {
        val missingFields = mutableListOf<String>()

        // Validate user details from the userDetailsMap
        validateFieldsForMap(userDetailsMap.value, missingFields, listOf(
            "shopName" to "Shop Name",
            "shopCategory" to "Category",
            "ownerName" to "Owner Name",
            "contactNumber" to "Contact Number",
            "emailAddress" to "Email Address",
            "shopDescription" to "Shop Description"
        ))

        // Validate location details from locationDetailsMap
        validateFieldsForMap(locationDetailsMap.value, missingFields, listOf(
            "fullAddress" to "User Location",
            "openingHour" to "Opening Hour",
            "closingMinute" to "Closing Minute",
            "closingHour" to "Closing Hour",
            "openingMinute" to "Opening Minute",
            //   "closedDays" to "Closed Days",
            "DeliveryAvailable" to "Delivery Availability",
            "DeliveryRadius" to "Delivery Radius"
        ))

        // Validate payment info from paymentInfoMap
        validateFieldsForMap(paymentInfoMap.value, missingFields, listOf(
            //    "paymentMethod" to "Payment Method",
            "bankName" to "Bank Name",
            "accountNumber" to "Account Number",
            "ifscCode" to "IFSC Code",
            "refundPolicy" to "Refund Policy"
        ))

        return Pair(missingFields.isEmpty(), missingFields)
    }

    // Helper function to validate fields in a map
    private fun validateFieldsForMap(map: HashMap<String, String>?, missingFields: MutableList<String>, fields: List<Pair<String, String>>) {
        fields.forEach { (field, fieldName) ->
            if (map?.get(field).isNullOrEmpty()) {
                missingFields.add(fieldName)
            }
        }
    }

    // Generic function to update a field in a specific HashMap (avoids redundancy)
    fun updateDetails(fieldMap: MutableLiveData<HashMap<String, String>>, field: String, value: String) {
        val updatedMap = fieldMap.value ?: hashMapOf()
        updatedMap[field] = value
        fieldMap.value = updatedMap
    }

    // Update methods for user, location, and payment info
    fun updateUserDetails(field: String, value: String) {
        updateDetails(userDetailsMap, field, value)
    }

    fun updateLocationDetails(field: String, value: String) {
        updateDetails(locationDetailsMap, field, value)
    }

    fun updatePaymentInfo(field: String, value: String) {
        updateDetails(paymentInfoMap, field, value)
    }

    // Function to add a new media file to the list of media files
    fun addMediaFile(uri: Uri) {
        val updatedMediaFiles = mediaFiles.value?.toMutableList() ?: mutableListOf()
        updatedMediaFiles.add(uri)
        mediaFiles.value = updatedMediaFiles
    }

    // Function to remove a media file from the list of media files
    fun removeMediaFile(uri: Uri) {
        val updatedMediaFiles = mediaFiles.value?.toMutableList() ?: mutableListOf()
        updatedMediaFiles.remove(uri)
        mediaFiles.value = updatedMediaFiles
    }

    // Function to clear all media files
    fun clearMediaFiles() {
        mediaFiles.value = mutableListOf()
    }


}
