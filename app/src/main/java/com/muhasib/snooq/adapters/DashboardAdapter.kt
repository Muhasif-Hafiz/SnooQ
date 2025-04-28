package com.muhasib.snooq.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muhasib.snooq.databinding.ItemDashboardBinding
import com.muhasib.snooq.model.DashboardItem


class DashboardAdapter(private val onItemClick: (DashboardItem) -> Unit) :
    ListAdapter<DashboardItem, DashboardAdapter.DashboardViewHolder>(DashboardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding = ItemDashboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class DashboardViewHolder(private val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(item: DashboardItem) {
            binding.ivIcon.setImageResource(item.resId)
            binding.tvTitle.text = item.title
        }
    }

    private class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardItem>() {
        override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
            return oldItem == newItem
        }
    }

}