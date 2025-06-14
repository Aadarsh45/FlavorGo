package com.example.flavorgo.ui.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flavorgo.R
import com.example.flavorgo.databinding.ActivityHomeBinding
import com.example.flavorgo.ui.fragments.CartFragment
import com.example.flavorgo.ui.fragments.FavoritesFragment
import com.example.flavorgo.ui.fragments.ProfileFragment
import com.example.flavorgo.ui.fragments.ScanDishFragment

import com.flavorgo.ui.fragments.HomeFragment


import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("HomeActivity", "onCreate called")

        // Ensure the default fragment loads only if no fragment exists
        if (supportFragmentManager.findFragmentById(R.id.frameLayout) == null) {
            loadFragment(ProfileFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_favorites -> FavoritesFragment()
                R.id.nav_cart -> CartFragment()
                R.id.nav_scan -> ScanDishFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> null
            }
            fragment?.let { loadFragment(it) }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}
