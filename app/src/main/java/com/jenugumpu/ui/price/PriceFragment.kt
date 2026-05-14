package com.jenugumpu.ui.price

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import com.jenugumpu.databinding.FragmentPriceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PriceFragment : Fragment() {

    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPriceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set last updated timestamp
        val fmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        binding.tvLastUpdated.text = "ಕೊನೆಯ ಬಾರಿ: ${fmt.format(Date())}"

        animateContent()
    }

    private fun animateContent() {
        // Stagger animate the price rows
        val rootLayout = binding.root
        rootLayout.alpha = 0f
        rootLayout.animate()
            .alpha(1f)
            .setDuration(400)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
