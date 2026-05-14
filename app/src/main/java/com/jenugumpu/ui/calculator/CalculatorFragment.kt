package com.jenugumpu.ui.calculator

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jenugumpu.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCalculate.setOnClickListener { calculate() }
    }

    private fun calculate() {
        val quantityStr    = binding.etCalcQuantity.text.toString().trim()
        val retailStr      = binding.etRetailPrice.text.toString().trim()
        val middlemanStr   = binding.etMiddlemanPrice.text.toString().trim()
        val filteringStr   = binding.etFilteringCost.text.toString().trim()
        val packagingStr   = binding.etPackagingCost.text.toString().trim()
        val transportStr   = binding.etTransportCost.text.toString().trim()

        if (quantityStr.isEmpty() || retailStr.isEmpty() || middlemanStr.isEmpty()) {
            Snackbar.make(binding.root, "⚠️ ಪ್ರಮಾಣ ಮತ್ತು ಬೆಲೆ ತುಂಬಿರಿ", Snackbar.LENGTH_SHORT).show()
            return
        }

        val qty        = quantityStr.toDoubleOrNull() ?: 0.0
        val retail     = retailStr.toDoubleOrNull() ?: 0.0
        val middleman  = middlemanStr.toDoubleOrNull() ?: 0.0
        val filtering  = filteringStr.toDoubleOrNull() ?: 0.0
        val packaging  = packagingStr.toDoubleOrNull() ?: 0.0
        val transport  = transportStr.toDoubleOrNull() ?: 0.0

        if (qty <= 0 || retail <= 0 || middleman <= 0) {
            Snackbar.make(binding.root, "⚠️ ಸರಿಯಾದ ಮೌಲ್ಯ ತುಂಬಿರಿ", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Calculations
        val totalCosts        = filtering + packaging + transport
        val middlemanIncome   = qty * middleman
        val retailRevenue     = qty * retail
        val netProfit         = retailRevenue - totalCosts
        val extraProfit       = netProfit - middlemanIncome

        // Update UI
        binding.tvMiddlemanTotal.text  = "₹${formatAmount(middlemanIncome)}"
        binding.tvDirectTotal.text     = "₹${formatAmount(netProfit)}"
        binding.tvExtraProfit.text     = "₹${formatAmount(extraProfit)}"
        binding.tvBreakdownRevenue.text   = "₹${formatAmount(retailRevenue)}"
        binding.tvBreakdownFiltering.text  = "-₹${formatAmount(filtering)}"
        binding.tvBreakdownPackaging.text  = "-₹${formatAmount(packaging)}"
        binding.tvBreakdownTransport.text  = "-₹${formatAmount(transport)}"
        binding.tvNetProfit.text       = "₹${formatAmount(netProfit)}"

        // Set profit colour
        val profitColor = if (netProfit >= 0) 0xFF16A34A.toInt() else 0xFFDC2626.toInt()
        binding.tvNetProfit.setTextColor(profitColor)
        binding.tvDirectTotal.setTextColor(profitColor)

        // Motivation message
        val profitPercent = if (middlemanIncome > 0) ((extraProfit / middlemanIncome) * 100).toInt() else 0
        binding.tvMotivation.text = when {
            extraProfit > 0 ->
                "🎉 ನೇರ ಮಾರಾಟದಲ್ಲಿ ${profitPercent}% ಹೆಚ್ಚು ಲಾಭ! ಮಧ್ಯವರ್ತಿ ಬಿಟ್ಟು ನೇರ ಮಾರಾಟ ಮಾಡಿ."
            extraProfit == 0.0 ->
                "➡️ ನೇರ ಮಾರಾಟ ಮತ್ತು ಸಗಟು ಬೆಲೆ ಒಂದೇ ಆಗಿದೆ."
            else ->
                "⚠️ ವೆಚ್ಚ ಹೆಚ್ಚಾಗಿದೆ. ಪ್ಯಾಕೇಜಿಂಗ್ ವೆಚ್ಚ ಕಡಿಮೆ ಮಾಡಿ."
        }

        // Show result with animation
        binding.resultsSection.visibility = View.VISIBLE
        binding.resultsSection.alpha = 0f
        binding.resultsSection.translationY = 40f
        binding.resultsSection.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animate the net profit counter
        ObjectAnimator.ofFloat(binding.tvNetProfit, "scaleX", 0.5f, 1.1f, 1f).apply {
            duration = 600
            start()
        }
        ObjectAnimator.ofFloat(binding.tvNetProfit, "scaleY", 0.5f, 1.1f, 1f).apply {
            duration = 600
            start()
        }
    }

    private fun formatAmount(value: Double): String {
        return when {
            value >= 100_000 -> String.format("%.1f ಲಕ್ಷ", value / 100_000)
            else             -> String.format("%.0f", value)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
