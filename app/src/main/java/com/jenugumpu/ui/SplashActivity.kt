package com.jenugumpu.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.jenugumpu.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startAnimations()
    }

    private fun startAnimations() {
        // Icon: drop in with bounce
        val iconScaleX = ObjectAnimator.ofFloat(binding.splashIcon, "scaleX", 0f, 1.1f, 1f)
        val iconScaleY = ObjectAnimator.ofFloat(binding.splashIcon, "scaleY", 0f, 1.1f, 1f)
        val iconAlpha  = ObjectAnimator.ofFloat(binding.splashIcon, "alpha", 0f, 1f)
        iconScaleX.duration = 700
        iconScaleY.duration = 700
        iconAlpha.duration  = 400
        iconScaleX.interpolator = BounceInterpolator()
        iconScaleY.interpolator = BounceInterpolator()

        // Title: slide up + fade in
        val titleTransY = ObjectAnimator.ofFloat(binding.splashTitle, "translationY", 60f, 0f)
        val titleAlpha  = ObjectAnimator.ofFloat(binding.splashTitle, "alpha", 0f, 1f)
        titleTransY.duration = 500
        titleAlpha.duration  = 500
        titleTransY.interpolator = DecelerateInterpolator()
        titleTransY.startDelay = 600

        // Tagline
        val taglineAlpha  = ObjectAnimator.ofFloat(binding.splashTagline, "alpha", 0f, 1f)
        val taglineTransY = ObjectAnimator.ofFloat(binding.splashTagline, "translationY", 30f, 0f)
        taglineAlpha.duration  = 400
        taglineTransY.duration = 400
        taglineAlpha.startDelay  = 900
        taglineTransY.startDelay = 900

        // Bee row
        val beeAlpha = ObjectAnimator.ofFloat(binding.beeRow, "alpha", 0f, 1f)
        beeAlpha.duration   = 400
        beeAlpha.startDelay = 1100

        // Progress bar
        val progressAlpha = ObjectAnimator.ofFloat(binding.splashProgress, "alpha", 0f, 1f)
        progressAlpha.duration   = 300
        progressAlpha.startDelay = 1300

        AnimatorSet().apply {
            playTogether(
                iconScaleX, iconScaleY, iconAlpha,
                titleTransY, titleAlpha,
                taglineAlpha, taglineTransY,
                beeAlpha, progressAlpha
            )
            start()
        }

        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2400)
    }
}
