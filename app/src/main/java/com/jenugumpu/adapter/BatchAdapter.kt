package com.jenugumpu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jenugumpu.databinding.ItemBatchBinding
import com.jenugumpu.model.BatchEntry
import com.jenugumpu.model.FloralSource
import java.text.SimpleDateFormat
import java.util.Locale

class BatchAdapter(
    private val onMarkSold: (BatchEntry) -> Unit,
    private val onDelete: (BatchEntry) -> Unit
) : ListAdapter<BatchEntry, BatchAdapter.BatchViewHolder>(DiffCallback()) {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("kn", "IN"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding = ItemBatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BatchViewHolder(private val binding: ItemBatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(batch: BatchEntry) {
            binding.tvBatchId.text = batch.batchId
            binding.tvBatchQuantity.text = "${batch.totalQuantityKg} ಕಿ.ಗ್ರಾ"
            binding.tvBatchJars.text = "${batch.jarCount} ಜಾರ್‌ಗಳು (${batch.jarSizeML}ಮಿ.ಲಿ)"
            binding.tvBatchGrade.text = "Grade ${batch.grade}"
            binding.tvBatchHarvester.text = batch.harvesterName
            binding.tvBatchDate.text = dateFormatter.format(batch.harvestDate)

            // Floral source
            val source = FloralSource.values().find { it.name == batch.floralSource }
            binding.tvBatchFloral.text = "${source?.emoji ?: "🌸"} ${source?.kannadaName ?: batch.floralSource}"

            // Status badge
            val (statusText, statusColor) = when (batch.status) {
                "SOLD"      -> "✓ ಮಾರಾಟ"  to 0xFF16A34A.toInt()
                "DELIVERED" -> "🚚 ತಲುಪಿತು" to 0xFF2563EB.toInt()
                else        -> "📦 ಪ್ಯಾಕ್"   to 0xFFF59E0B.toInt()
            }
            binding.tvBatchStatus.text = statusText
            binding.tvBatchStatus.setBackgroundColor(statusColor)

            // Hide mark sold if already sold/delivered
            if (batch.status != "PACKED") {
                binding.btnMarkSold.isEnabled = false
                binding.btnMarkSold.alpha = 0.4f
            } else {
                binding.btnMarkSold.isEnabled = true
                binding.btnMarkSold.alpha = 1f
            }

            binding.btnMarkSold.setOnClickListener { onMarkSold(batch) }
            binding.btnDeleteBatch.setOnClickListener { onDelete(batch) }

            binding.root.alpha = 0f
            binding.root.animate().alpha(1f).setDuration(300).start()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BatchEntry>() {
        override fun areItemsTheSame(a: BatchEntry, b: BatchEntry) = a.id == b.id
        override fun areContentsTheSame(a: BatchEntry, b: BatchEntry) = a == b
    }
}
