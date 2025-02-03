import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShopRegistrationViewModel : ViewModel() {

    // HashMaps for different fragments
    val userDetailsMap = MutableLiveData<HashMap<String, String>>(hashMapOf())
    val locationDetailsMap = MutableLiveData<HashMap<String, String>>(hashMapOf())
    val paymentInfoMap = MutableLiveData<HashMap<String, String>>(hashMapOf())
    val mediaFiles = MutableLiveData<HashMap<String, String>>(hashMapOf())


    private val _locationDetails = MutableLiveData<Map<String, String>>()
    val locationDetails: LiveData<Map<String, String>> = _locationDetails
    val socialMediaLinks = MutableLiveData<List<String>>()
    private val _shopImageLinks = MutableLiveData<List<String>>()
    val shopImageLinks: LiveData<List<String>> = _shopImageLinks

    init {
        _shopImageLinks.value = emptyList()
    }

    // Method to add new links to the list
    fun addShopImageLinks(link: String) {
        val currentList = _shopImageLinks.value?.toMutableList() ?: mutableListOf()
        currentList.add(link)  // Add the new link to the list
        _shopImageLinks.value = currentList  // Update LiveData
    }
    fun addSocialMediaLink(instagram: String, facebook: String, other: String) {
        socialMediaLinks.value = listOf(instagram, facebook, other)
    }
   // , link2 :String, link3 :String, link4 :String, link5 :String, link6 : String




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
            "openingTime" to "Opening Time",
            "closingTime" to "Closing Time",
//            "closingHour" to "Closing Hour",
//            "openingMinute" to "Opening Minute",
              "selectedDays" to "selectedDays",
        //    "DeliveryAvailable" to "Delivery Availability",
         //   "DeliveryRadius" to "Delivery Radius"
        ))

        // Validate payment info from paymentInfoMap
        validateFieldsForMap(paymentInfoMap.value, missingFields, listOf(
               "paymentMethod" to "Payment Method",
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


    fun updateDetails(fieldMap: MutableLiveData<HashMap<String, String>>, field: String, value: String) {
        val updatedMap = fieldMap.value ?: hashMapOf()
        updatedMap[field] = value
        fieldMap.value = updatedMap
    }

    // Update methods for user, location, and payment info
    fun updateUserDetails(field: String, value: String) {
        updateDetails(userDetailsMap, field, value)
    }
    fun updateShopMediaDetails(field  : String , value : String){
        updateDetails(mediaFiles, field, value)
    }

    fun updateLocationDetails(field: String, value: String) {




        updateDetails(locationDetailsMap, field, value)
    }

    fun updatePaymentInfo(field: String, value: String) {


        updateDetails(paymentInfoMap, field, value)
    }




}
