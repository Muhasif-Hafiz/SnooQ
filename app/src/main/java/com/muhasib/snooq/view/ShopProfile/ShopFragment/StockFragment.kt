package com.muhasib.snooq.view.ShopProfile.ShopFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muhasib.snooq.Base.MySharedPreferences
import com.muhasib.snooq.R
import com.muhasib.snooq.adapters.ProductAdapter
import com.muhasib.snooq.databinding.FragmentStockBinding
import com.muhasib.snooq.model.ProductCategory

class StockFragment : Fragment() {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var productAdapter: ProductAdapter
    private val allProducts = mutableListOf<ProductCategory>()
    private val filteredProducts = mutableListOf<ProductCategory>()
    private val categories = listOf(
        "All", "Electronics", "Computers", "Mobile", "Accessories",
        "Cameras", "Audio", "Gaming", "Wearable", "Smart Home"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setupShopData()
        loadSampleProducts()
        setupCategoryChips()
        setupProductsRecyclerView()
        setupFloatingActionButton()
        calculateAndDisplayStatistics() // Added statistics calculation
    }

    private fun setupShopData() {
        binding.textShopName.text = "Tech Store"
        binding.textStockCountValue.text = allProducts.size.toString()
        binding.textCategoryCount.text = (categories.size - 1).toString()
    }

    private fun loadSampleProducts() {
        allProducts.clear()

        // Sample product data with added stock quantities
        allProducts.addAll(listOf(
            ProductCategory("1", "MacBook Pro 16\"", "Apple M1 Max, 32GB RAM, 1TB SSD", "Computers", 2399.99, 5),
            ProductCategory("2", "Samsung Galaxy S22", "6.1\" Dynamic AMOLED 2X, 128GB", "Mobile", 899.99, 12),
            ProductCategory("3", "Sony WH-1000XM4", "Wireless Noise Cancelling Headphones", "Audio", 349.99, 8),
            ProductCategory("4", "NVIDIA GeForce RTX 3080", "10GB GDDR6X Graphics Card", "Gaming", 699.99, 3),
            ProductCategory("5", "iPad Pro 12.9\"", "M1 chip, 256GB, Wi-Fi + Cellular", "Electronics", 1199.99, 6),
            ProductCategory("6", "Apple Watch Series 7", "45mm, GPS + Cellular, Aluminum Case", "Wearable", 499.99, 15),
            ProductCategory("7", "Google Nest Hub Max", "10\" Smart Display with Google Assistant", "Smart Home", 229.99, 9),
            ProductCategory("8", "Canon EOS R5", "45MP Full-Frame Mirrorless Camera", "Cameras", 3899.99, 2),
            ProductCategory("9", "Bose QuietComfort Earbuds", "Noise Cancelling True Wireless Earbuds", "Audio", 279.99, 7),
            ProductCategory("10", "Dell XPS 15", "11th Gen Intel i9, 32GB RAM, 1TB SSD", "Computers", 2199.99, 4)
        ))

        filteredProducts.clear()
        filteredProducts.addAll(allProducts)
    }

    private fun calculateAndDisplayStatistics() {
        // Calculate total stock items
        val totalStockItems = allProducts.sumOf { it.stockQuantity ?: 0 }
        binding.textStockCountValue.text = totalStockItems.toString()

        // Calculate top selling items (products with stock < 5)
        val lowStockThreshold = 5
        val lowSellingItems = allProducts.count { it.stockQuantity ?: 0 < lowStockThreshold }
        binding.textLowSellingCount.text = lowSellingItems.toString()

        // Calculate top selling items (products with stock > 10)
        val highStockThreshold = 10
        val topSellingItems = allProducts.count { it.stockQuantity ?: 0 > highStockThreshold }
        binding.textTopSellingCount.text = topSellingItems.toString()

        // Calculate total inventory value
        val totalInventoryValue = allProducts.sumOf { (it.price ?: 0.0) * (it.stockQuantity ?: 0) }

        // You could add another card for inventory value if you want:
        // binding.textInventoryValue.text = "$${"%.2f".format(totalInventoryValue)}"
    }

    private fun setupCategoryChips() {
        val chipGroup = binding.chipGroupCategories
        chipGroup.removeAllViews()

        categories.forEach { category ->
            val chip = layoutInflater.inflate(R.layout.chip_category_item, chipGroup, false) as Chip
            chip.text = category
            chip.isChecked = category == "All"
            chip.setOnClickListener { filterProductsByCategory(category) }
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == View.NO_ID) {
                (group.getChildAt(0) as? Chip)?.isChecked = true
            }
        }
    }

    private fun filterProductsByCategory(category: String) {
        filteredProducts.clear()
        filteredProducts.addAll(
            if (category == "All") allProducts
            else allProducts.filter { it.category == category }
        )
        productAdapter.notifyDataSetChanged()
        calculateAndDisplayStatistics() // Update stats when filtering
    }

    private fun setupProductsRecyclerView() {
        binding.recyclerItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            productAdapter = ProductAdapter(filteredProducts).also { adapter = it }
        }
    }

    private fun setupFloatingActionButton() {
        binding.fabAdd.setOnClickListener { showAddOptionsDialog() }
    }

    private fun showAddOptionsDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Item")
            .setItems(arrayOf("Upload via Camera", "Scan Product")) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> scanProduct()
                }
            }
            .show()
    }

    private fun openCamera() {

        findNavController().navigate(R.id.action_stockFragment2_to_productListingFragment)
    }

    private fun scanProduct() {
        Toast.makeText(requireContext(), "Opening scanner...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}