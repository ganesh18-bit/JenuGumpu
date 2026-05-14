package com.jenugumpu.ui.home

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jenugumpu.R
import com.jenugumpu.adapter.HarvestAdapter
import com.jenugumpu.databinding.FragmentHomeBinding
import com.jenugumpu.ui.MainActivity
import com.jenugumpu.ui.harvest.HarvestViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HarvestViewModel by activityViewModels()
    private lateinit var harvestAdapter: HarvestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        setupClickListeners()
        animateCards()
    }

    private fun setupRecyclerView() {
        harvestAdapter = HarvestAdapter { entry -> viewModel.deleteHarvest(entry) }
        binding.rvRecentHarvests.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = harvestAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeData() {
        viewModel.recentHarvests.observe(viewLifecycleOwner) { harvests ->
            harvestAdapter.submitList(harvests)
        }
        viewModel.totalStock.observe(viewLifecycleOwner) { total ->
            val kg = total ?: 0.0
            binding.tvTotalStock.text = String.format("%.1f", kg)
            binding.tvCollectiveStock.text = "${String.format("%.1f", kg)} ಕಿ.ಗ್ರಾ"
            binding.tvEstimatedValue.text = "≈ ₹${(kg * 350).toInt()}"
            val progress = ((kg / 500.0) * 100).toInt().coerceIn(0, 100)
            ObjectAnimator.ofInt(binding.stockProgress, "progress", 0, progress).apply {
                duration = 1000; interpolator = DecelerateInterpolator(); start()
            }
        }
        viewModel.memberCount.observe(viewLifecycleOwner) { count ->
            animateCounter(binding.tvMemberCount, count ?: 0)
        }
        viewModel.todayHarvest.observe(viewLifecycleOwner) { today ->
            binding.tvTodayHarvest.text = String.format("%.1f", today ?: 0.0)
        }
    }

    private fun animateCounter(tv: TextView, target: Int) {
        ValueAnimator.ofInt(0, target).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
            addUpdateListener { tv.text = it.animatedValue.toString() }
            start()
        }
    }

    private fun setupClickListeners() {
        binding.btnAddHarvest.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.nav_harvest)
        }
        binding.btnCheckQuality.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.nav_grading)
        }
        binding.btnViewPrices.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.nav_price)
        }
        binding.btnCalculator.setOnClickListener {
            (activity as? MainActivity)?.openCalculator()
        }
        binding.tvViewAll.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.nav_harvest)
        }
    }

    private fun animateCards() {
        listOf(binding.cardTotalStock, binding.cardMembers,
               binding.cardToday, binding.cardCollective)
            .forEachIndexed { i, card ->
                card.alpha = 0f; card.translationY = 60f
                card.animate().alpha(1f).translationY(0f)
                    .setDuration(400).setStartDelay((i * 100 + 100).toLong())
                    .setInterpolator(DecelerateInterpolator()).start()
            }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
