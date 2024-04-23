package com.balius.coincap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.balius.coincap.databinding.ActivityMainBinding
import com.balius.coincap.ui.coins.CoinsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding :ActivityMainBinding
    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController





    }

    override fun onBackPressed() {
        val currentDestination = navController.currentDestination?.id

        // Check if the current destination is the CoinFragment
        if (currentDestination == R.id.coinsFragment) {
            // If the current destination is CoinFragment, finish the activity
            finishAffinity()
        } else {
            // If the current destination is not CoinFragment, proceed with default back press behavior
            super.onBackPressed()
        }
    }




}