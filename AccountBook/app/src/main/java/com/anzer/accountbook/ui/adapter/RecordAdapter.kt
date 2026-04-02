package com.anzer.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anzer.accountbook.data.model.Record
import com.anzer.accountbook.databinding.ItemRecordBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter(private val onDelete: (Record) -> Unit) : ListAdapter<Record, RecordAdapter.ViewHolder>(DiffCallback()) {
    private val df = DecimalFormat("#,##0.00")
    private val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: Record) {
            binding.tvCategory.text = record.category
            binding.tvNote.text = record.note.ifEmpty { "No note" }
            binding.tvDate.text = sdf.format(Date(record.date))
            binding.tvCategoryIcon.text = getCategoryIcon(record.category)
            if (record.type == 1) {
                binding.tvAmount.text = "+$${df.format(record.amount)}"
                binding.tvAmount.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
            } else {
                binding.tvAmount.text = "-$${df.format(record.amount)}"
                binding.tvAmount.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
            }
            binding.btnDelete.setOnClickListener { onDelete(record) }
        }
    }

    private fun getCategoryIcon(category: String): String = when (category) {
        "Catering" -> "🍜"; "Transport" -> "🚗"; "Shopping" -> "🛍️"; "Entertainment" -> "🎮"
        "Medical" -> "💊"; "Housing" -> "🏠"; "Education" -> "📚"; "Salary" -> "💰"; "Bonus" -> "🎁"
        else -> "📝"
    }

    class DiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Record, newItem: Record) = oldItem == newItem
    }
}
