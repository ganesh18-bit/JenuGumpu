package com.jenugumpu.ui.batch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.jenugumpu.adapter.BatchAdapter
import com.jenugumpu.databinding.DialogAddBatchBinding
import com.jenugumpu.databinding.FragmentBatchBinding
import com.jenugumpu.model.BatchEntry
import com.jenugumpu.model.FloralSource
import kotlinx.coroutines.launch

class BatchFragment : Fragment() {

    private var _binding: FragmentBatchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BatchViewModel by activityViewModels()
    private lateinit var adapter: BatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        binding.fabAddBatch.setOnClickListener { showAddBatchDialog() }
    }

    private fun setupRecyclerView() {
        adapter = BatchAdapter(
            onMarkSold = { batch ->
                viewModel.updateBatchStatus(batch.batchId, "SOLD")
                Snackbar.make(binding.root, "✅ ${batch.batchId} ಮಾರಾಟ!", Snackbar.LENGTH_SHORT).show()
            },
            onDelete = { batch ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("ಬ್ಯಾಚ್ ಅಳಿಸಿ")
                    .setMessage("${batch.batchId} ಅಳಿಸಲು ಖಚಿತವೇ?")
                    .setPositiveButton("ಹೌದು") { _, _ ->
                        viewModel.deleteBatch(batch)
                        Snackbar.make(binding.root, "ಅಳಿಸಲಾಗಿದೆ", Snackbar.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("ಇಲ್ಲ", null).show()
            }
        )
        binding.rvBatches.layoutManager = LinearLayoutManager(context)
        binding.rvBatches.adapter = adapter
    }

    private fun observeData() {
        viewModel.allBatches.observe(viewLifecycleOwner) { batches ->
            val list = batches ?: emptyList()
            adapter.submitList(list)
            val empty = list.isEmpty()
            binding.batchEmptyState.visibility = if (empty) View.VISIBLE else View.GONE
            binding.rvBatches.visibility        = if (empty) View.GONE   else View.VISIBLE
            binding.tvTotalBatches.text = list.size.toString()
            binding.tvPackedCount.text  = list.count { it.status == "PACKED" }.toString()
            binding.tvSoldCount.text    = list.count { it.status == "SOLD" || it.status == "DELIVERED" }.toString()
        }
    }

    private fun showAddBatchDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val db = DialogAddBatchBinding.inflate(layoutInflater)
        dialog.setContentView(db.root)

        // Generate batch ID synchronously for display, then use it on save
        viewLifecycleOwner.lifecycleScope.launch {
            val batchId = viewModel.generateBatchId()
            db.tvAutoBatchId.text = "ID: $batchId"

            db.btnBatchSave.setOnClickListener {
                val harvester = db.etBatchHarvester.text?.toString()?.trim() ?: ""
                val location  = db.etBatchLocation.text?.toString()?.trim() ?: ""
                val qtyStr    = db.etBatchQuantity.text?.toString()?.trim() ?: ""
                val jarsStr   = db.etJarCount.text?.toString()?.trim() ?: "0"
                val sizeStr   = db.etJarSize.text?.toString()?.trim() ?: "500"

                if (harvester.isEmpty() || location.isEmpty() || qtyStr.isEmpty()) {
                    Snackbar.make(binding.root, "⚠️ ಎಲ್ಲಾ ವಿವರ ತುಂಬಿರಿ", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val qty  = qtyStr.toDoubleOrNull() ?: 0.0
                val jars = jarsStr.toIntOrNull() ?: 0
                val size = sizeStr.toIntOrNull() ?: 500

                val floralSource = when {
                    db.chipBCoffee.isChecked     -> FloralSource.COFFEE_BLOSSOM.name
                    db.chipBWild.isChecked        -> FloralSource.WILDFLOWER.name
                    db.chipBEucalyptus.isChecked  -> FloralSource.EUCALYPTUS.name
                    db.chipBMulti.isChecked       -> FloralSource.MULTIFLORAL.name
                    else                          -> FloralSource.COFFEE_BLOSSOM.name
                }
                val grade = when (db.rgBatchGrade.checkedRadioButtonId) {
                    db.rbBatchA.id -> "A"
                    db.rbBatchB.id -> "B"
                    else           -> "C"
                }

                val now = System.currentTimeMillis()
                viewModel.insertBatch(
                    BatchEntry(
                        batchId         = batchId,
                        grade           = grade,
                        floralSource    = floralSource,
                        totalQuantityKg = qty,
                        jarCount        = jars,
                        jarSizeML       = size,
                        harvestDate     = now,
                        packagingDate   = now,
                        location        = location,
                        harvesterName   = harvester,
                        status          = "PACKED"
                    )
                )
                dialog.dismiss()
                Snackbar.make(binding.root, "📦 $batchId ರಚಿಸಲಾಗಿದೆ!", Snackbar.LENGTH_LONG).show()
            }
        }

        db.btnBatchCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
