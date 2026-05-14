package com.jenugumpu.ui.harvest

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.jenugumpu.adapter.HarvestAdapter
import com.jenugumpu.databinding.DialogAddHarvestBinding
import com.jenugumpu.databinding.FragmentHarvestBinding
import com.jenugumpu.model.FloralSource
import com.jenugumpu.model.HarvestEntry
import java.text.SimpleDateFormat
import java.util.*

class HarvestFragment : Fragment() {

    private var _binding: FragmentHarvestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HarvestViewModel by activityViewModels()
    private lateinit var adapter: HarvestAdapter
    private var selectedDate = System.currentTimeMillis()
    private val dateFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHarvestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeAll()                 // start with all harvests
        setupChipFilters()
        binding.fabAddHarvest.setOnClickListener { showAddDialog() }
    }

    private fun setupRecyclerView() {
        adapter = HarvestAdapter { entry ->
            viewModel.deleteHarvest(entry)
            Snackbar.make(binding.root, "✓ ದಾಖಲೆ ಅಳಿಸಲಾಗಿದೆ", Snackbar.LENGTH_SHORT).show()
        }
        binding.rvHarvests.layoutManager = LinearLayoutManager(context)
        binding.rvHarvests.adapter = adapter
    }

    private fun observeAll() {
        viewModel.allHarvests.observe(viewLifecycleOwner) { list ->
            val data = list ?: emptyList()
            adapter.submitList(data)
            binding.emptyState.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHarvests.visibility  = if (data.isEmpty()) View.GONE  else View.VISIBLE
        }
    }

    private fun observeFiltered(liveData: androidx.lifecycle.LiveData<List<HarvestEntry>>) {
        // Remove all existing observers and add the new one
        viewModel.allHarvests.removeObservers(viewLifecycleOwner)
        viewModel.getHarvestsByGrade("A").removeObservers(viewLifecycleOwner)
        viewModel.getHarvestsByGrade("B").removeObservers(viewLifecycleOwner)
        viewModel.getHarvestsBySource(FloralSource.COFFEE_BLOSSOM.name).removeObservers(viewLifecycleOwner)
        viewModel.getHarvestsBySource(FloralSource.WILDFLOWER.name).removeObservers(viewLifecycleOwner)
        liveData.observe(viewLifecycleOwner) { list ->
            val data = list ?: emptyList()
            adapter.submitList(data)
            binding.emptyState.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHarvests.visibility  = if (data.isEmpty()) View.GONE  else View.VISIBLE
        }
    }

    private fun setupChipFilters() {
        binding.chipAll.setOnCheckedChangeListener { _, checked ->
            if (checked) observeFiltered(viewModel.allHarvests)
        }
        binding.chipGradeA.setOnCheckedChangeListener { _, checked ->
            if (checked) observeFiltered(viewModel.getHarvestsByGrade("A"))
        }
        binding.chipGradeB.setOnCheckedChangeListener { _, checked ->
            if (checked) observeFiltered(viewModel.getHarvestsByGrade("B"))
        }
        binding.chipCoffee.setOnCheckedChangeListener { _, checked ->
            if (checked) observeFiltered(viewModel.getHarvestsBySource(FloralSource.COFFEE_BLOSSOM.name))
        }
        binding.chipWildflower.setOnCheckedChangeListener { _, checked ->
            if (checked) observeFiltered(viewModel.getHarvestsBySource(FloralSource.WILDFLOWER.name))
        }
    }

    private fun showAddDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val db = DialogAddHarvestBinding.inflate(layoutInflater)
        dialog.setContentView(db.root)

        selectedDate = System.currentTimeMillis()
        db.etDate.setText(dateFmt.format(Date(selectedDate)))

        val pickDate = {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                selectedDate = cal.timeInMillis
                db.etDate.setText(dateFmt.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        db.etDate.setOnClickListener { pickDate() }
        db.inputDate.setEndIconOnClickListener { pickDate() }

        db.btnSave.setOnClickListener {
            val name     = db.etHarvesterName.text?.toString()?.trim() ?: ""
            val location = db.etLocation.text?.toString()?.trim() ?: ""
            val qtyStr   = db.etQuantity.text?.toString()?.trim() ?: ""

            if (name.isEmpty() || location.isEmpty() || qtyStr.isEmpty()) {
                Snackbar.make(binding.root, "⚠️ ಎಲ್ಲಾ ವಿವರ ತುಂಬಿರಿ", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val qty = qtyStr.toDoubleOrNull()
            if (qty == null || qty <= 0) {
                Snackbar.make(binding.root, "⚠️ ಸರಿಯಾದ ಪ್ರಮಾಣ ತುಂಬಿರಿ", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val floralSource = when {
                db.chipCoffee.isChecked      -> FloralSource.COFFEE_BLOSSOM.name
                db.chipWildflower.isChecked  -> FloralSource.WILDFLOWER.name
                db.chipEucalyptus.isChecked  -> FloralSource.EUCALYPTUS.name
                db.chipMustard.isChecked     -> FloralSource.MUSTARD.name
                db.chipMultifloral.isChecked -> FloralSource.MULTIFLORAL.name
                db.chipJamun.isChecked       -> FloralSource.JAMUN.name
                else                         -> FloralSource.WILDFLOWER.name
            }
            val grade = when (db.rgGrade.checkedRadioButtonId) {
                db.rbGradeA.id -> "A"
                db.rbGradeB.id -> "B"
                else           -> "C"
            }

            viewModel.insertHarvest(
                HarvestEntry(
                    harvesterName = name,
                    date          = selectedDate,
                    location      = location,
                    quantityKg    = qty,
                    floralSource  = floralSource,
                    honeyColor    = "AMBER",
                    grade         = grade,
                    notes         = db.etNotes.text?.toString()?.trim() ?: ""
                )
            )
            dialog.dismiss()
            Snackbar.make(binding.root, "✅ ಸಂಗ್ರಹ ಉಳಿಸಲಾಗಿದೆ!", Snackbar.LENGTH_SHORT).show()
        }
        db.btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
