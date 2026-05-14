package com.jenugumpu.ui.grading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.jenugumpu.databinding.FragmentGradingBinding

class GradingFragment : Fragment() {

    private var _binding: FragmentGradingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGradingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMoistureSeekBar()
        updateMoistureResult(15) // default 15%
    }

    private fun setupMoistureSeekBar() {
        binding.seekbarMoisture.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val moisture = progress + 10   // maps 0–30 → 10%–40%
                updateMoistureResult(moisture)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateMoistureResult(moisture: Int) {
        binding.tvMoistureValue.text = "$moisture%"

        when {
            moisture < 17 -> {
                binding.tvMoistureGrade.text = "🥇 ಗ್ರೇಡ್ A"
                binding.tvMoistureGrade.setTextColor(0xFF15803D.toInt())
                binding.tvMoistureAdvice.text = "✅ ಉತ್ತಮ! ಈ ಜೇನು ಮಾರಾಟಕ್ಕೆ ಸಿದ್ಧ."
                binding.tvMoistureAdvice.setTextColor(0xFF15803D.toInt())
                binding.moistureResultCard.setBackgroundColor(0xFFFEF3C7.toInt())
            }
            moisture <= 20 -> {
                binding.tvMoistureGrade.text = "🥈 ಗ್ರೇಡ್ B"
                binding.tvMoistureGrade.setTextColor(0xFFD97706.toInt())
                binding.tvMoistureAdvice.text = "⚠️ ಮಧ್ಯಮ ಗ್ರೇಡ್. ಶೋಧನ ಮಾಡಿ ಬ್ಯಾಚ್ ಮಾಡಿ."
                binding.tvMoistureAdvice.setTextColor(0xFFD97706.toInt())
                binding.moistureResultCard.setBackgroundColor(0xFFFFF7ED.toInt())
            }
            else -> {
                binding.tvMoistureGrade.text = "🥉 ಗ್ರೇಡ್ C"
                binding.tvMoistureGrade.setTextColor(0xFFDC2626.toInt())
                binding.tvMoistureAdvice.text = "❌ ತೇವಾಂಶ ಹೆಚ್ಚಾಗಿದೆ. ಒಣಗಿಸಿ ನಂತರ ಮಾರಾಟ ಮಾಡಿ."
                binding.tvMoistureAdvice.setTextColor(0xFFDC2626.toInt())
                binding.moistureResultCard.setBackgroundColor(0xFFFEF9C3.toInt())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
