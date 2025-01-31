import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.muhasib.snooq.model.CustomerReview
import com.muhasib.snooq.model.LocationDetails
import com.muhasib.snooq.model.Offer
import com.muhasib.snooq.model.PaymentInfo
import com.muhasib.snooq.model.Product
import com.muhasib.snooq.model.Shop
import com.muhasib.snooq.model.Shopkeeper
import com.muhasib.snooq.model.UserDetails
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

            // Create a list of shops for the shopkeeper
            val shops: List<Shop> = listOf(
                Shop(
                    shopId = "shop_id_001",
                    shopName = userDetails["shopName"].toString(),
                    shopCategory = userDetails["shopCategory"].toString(),
                    description = userDetails["shopDescription"].toString(),
                    address = locationDetails["fullAddress"].toString(),
                    location = LocationDetails(
                        fullAddress = locationDetails["fullAddress"].toString(),
                        latitude = locationDetails["latitude"]?.toDouble(),
                        longitude = locationDetails["longitude"]?.toDouble(),
                        openingTime = locationDetails["openingTime"].toString(),
                        closingTime = locationDetails["closingTime"].toString(),
                        selectedDays = locationDetails["selectedDays"].toString(),
                        DeliveryAvailable = locationDetails["DeliveryAvailable"].toBoolean(),
                        DeliveryRadius = locationDetails["DeliveryRadius"].toString()
                    ),
                    currentVisitors = 0,
                    visitCount = 0,
                    averageRating = 0.0,
                    ratingCount = 300,
                    offers = listOf(
                        Offer(
                            title = "10% Off",
                            description = "On all items above $50",
                            discount = 10,
                            expiryDate = "2024-01-31"
                        )
                    ),
                    products = listOf(
                        Product(
                            productId = "prod_001",
                            name = "Milk",
                            description = "1L of fresh milk",
                            price = 2.5,
                            stockQuantity = 50,
                            imageUrl = "https://example.com/milk.jpg",
                            category = "Dairy"
                        )
                    ),
                    qrCode = "https://example.com/qrcode/shop_001",
                    customerReviews = listOf(
                        CustomerReview(
                            userId = "user_001",
                            rating = 5,
                            comment = "Great shop!",
                            date = "2024-01-10"
                        )
                    ),
                    isVerified = false,
                    verificationDocuments = listOf("https://example.com/license.jpg"),
                    subscriptionPlan = "Premium",
                    subscriptionExpiry = "2025-01-01",
                    tags = listOf("organic", "local"),
                    shopImages = listOf("https://example.com/shop1.jpg", "https://example.com/shop2.jpg")
                )
            )

            // Create PaymentInfo object
            val paymentInfo = PaymentInfo(
                paymentMethod = paymentDetails["paymentMethod"].toString(),
                bankName = paymentDetails["bankName"].toString(),
                accountNumber = paymentDetails["accountNumber"].toString(),
                ifscCode = paymentDetails["ifscCode"].toString(),
                refundPolicy = paymentDetails["refundPolicy"].toString()
            )

            // Create Shopkeeper data with bank details
            val shopkeepers: HashMap<String, Shopkeeper> = hashMapOf(
                "shopkeeper_id_123" to Shopkeeper(
                    shopkeeperId = "shopkeeper_id_123",
                    name = userDetails["ownerName"].toString(),
                    email = userDetails["emailAddress"].toString(),
                    phoneNumber = userDetails["contactNumber"].toString(),
                    profilePicture = "null", // Assuming no profile picture URL is provided
                    registrationDate = "2024-01-01",
                    paymentInfo = paymentInfo, // Add paymentInfo
                    shops = shops // Adding the list of shops
                )
            )

            // Upload the structured data
            lifecycleOwner.lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    shopRegistrationRepository.uploadShopDetails(shopkeepers)
                }

                // Show success or failure message
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
