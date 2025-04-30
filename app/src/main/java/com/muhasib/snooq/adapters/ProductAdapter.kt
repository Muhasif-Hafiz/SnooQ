package com.muhasib.snooq.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.muhasib.snooq.R
import com.muhasib.snooq.model.ProductCategory

class ProductAdapter(private val products: List<ProductCategory>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage = view.findViewById<android.widget.ImageView>(R.id.image_product)
        val productName = view.findViewById<android.widget.TextView>(R.id.text_product_name)
        val productDescription = view.findViewById<android.widget.TextView>(R.id.text_product_description)
        val productPrice = view.findViewById<android.widget.TextView>(R.id.text_product_price)
        val productStock = view.findViewById<android.widget.TextView>(R.id.text_product_stock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Set product data
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = "$${String.format("%.2f", product.price)}"

        // Set stock status
        if (product.stockQuantity > 0) {
            holder.productStock.text = "In Stock"
            holder.productStock.setBackgroundResource(R.drawable.stock_background)
        } else {
            holder.productStock.text = "Out of Stock"
            holder.productStock.setBackgroundResource(R.drawable.out_of_stock_background)
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Selected: ${product.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount() = products.size
}