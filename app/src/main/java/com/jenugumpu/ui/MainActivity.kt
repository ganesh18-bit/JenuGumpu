package com.jenugumpu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jenugumpu.R
import com.jenugumpu.databinding.ActivityMainBinding
import com.jenugumpu.ui.batch.BatchFragment
import com.jenugumpu.ui.calculator.CalculatorFragment
import com.jenugumpu.ui.grading.GradingFragment
import com.jenugumpu.ui.harvest.HarvestFragment
import com.jenugumpu.ui.home.HomeFragment
import com.jenugumpu.ui.price.PriceFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), animate = false)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> { loadFragment(HomeFragment());    true }
                R.id.nav_harvest -> { loadFragment(HarvestFragment()); true }
                R.id.nav_grading -> { loadFragment(GradingFragment()); true }
                R.id.nav_price   -> { loadFragment(PriceFragment());   true }
                R.id.nav_batch   -> { loadFragment(BatchFragment());   true }
                else             -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, animate: Boolean = true) {
        val tx = supportFragmentManager.beginTransaction()
        if (animate) {
            tx.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        tx.replace(R.id.fragment_container, fragment)
        tx.commitAllowingStateLoss()
    }

    /** Called by HomeFragment quick-action buttons */
    fun navigateTo(tabId: Int) {
        binding.bottomNavigation.selectedItemId = tabId
    }

    /** Navigate to Calculator (not in bottom nav) */
    fun openCalculator() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.fragment_container, CalculatorFragment())
            .commitAllowingStateLoss()
    }
}
