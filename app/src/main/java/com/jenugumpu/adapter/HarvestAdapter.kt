package com.jenugumpu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jenugumpu.databinding.ItemHarvestBinding
import com.jenugumpu.model.FloralSource
import com.jenugumpu.model.HarvestEntry
import java.text.SimpleDateFormat
import java.util.Locale

class HarvestAdapter(
    private val onDelete: (HarvestEntry) -> Unit
) : ListAdapter<HarvestEntry, HarvestAdapter.HarvestViewHolder>(DiffCallback()) {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale("kn", "IN"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarvestViewHolder {
        val binding = ItemHarvestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HarvestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HarvestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HarvestViewHolder(private val binding: ItemHarvestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: HarvestEntry) {
            binding.tvHarvesterName.text = entry.harvesterName
            binding.tvLocation.text = "📍 ${entry.location}"
            binding.tvQuantity.text = "${entry.quantityKg} ಕಿ.ಗ್ರಾ"
            binding.tvDate.text = dateFormatter.format(entry.date)
            binding.tvGradeBadge.text = entry.grade

            // Set grade badge colour
            val gradeColor = when (entry.grade) {
                "A" -> 0xFFF59E0B.toInt()
                "B" -> 0xFFFB923C.toInt()
                else -> 0xFFA16207.toInt()
            }
            binding.tvGradeBadge.setBackgroundColor(gradeColor)

            // Floral source emoji
            val source = FloralSource.values().find { it.name == entry.floralSource }
            binding.tvFloralEmoji.text = source?.emoji ?: "🌸"
            binding.tvFloralSource.text = source?.kannadaName ?: entry.floralSource

            binding.btnDelete.setOnClickListener { onDelete(entry) }

            // Animate item in
            binding.root.alpha = 0f
            binding.root.animate().alpha(1f).setDuration(300).start()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HarvestEntry>() {
        override fun areItemsTheSame(a: HarvestEntry, b: HarvestEntry) = a.id == b.id
        override fun areContentsTheSame(a: HarvestEntry, b: HarvestEntry) = a == b
    }
}
