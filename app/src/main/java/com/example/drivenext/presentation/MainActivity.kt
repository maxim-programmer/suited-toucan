package com.example.drivenext.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.example.drivenext.DriveNextApp
import com.example.drivenext.R
import com.example.drivenext.databinding.ActivityMainBinding
import com.example.drivenext.presentation.common.navigateToTab

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var systemInsets: Insets = Insets.NONE

    private val deepLinkHandler
        get() = (application as DriveNextApp).container.authDeepLinkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            applyInsets()
            insets
        }

        setupBottomNavigation()

        if (savedInstanceState == null) {
            deepLinkHandler.handle(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkHandler.handle(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            navController.navigateToTab(item.itemId)
            true
        }
        binding.bottomNavigation.setOnItemReselectedListener { item ->
            navController.navigateToTab(item.itemId)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val tabId = resolveTab(destination)
            binding.bottomBarContainer.isVisible = tabId != null
            if (tabId != null) {
                binding.bottomNavigation.menu.findItem(tabId)?.isChecked = true
            }
            applyInsets()
        }
    }

    private fun applyInsets() {
        val bars = systemInsets
        val barVisible = binding.bottomBarContainer.isVisible
        binding.navHostFragment.setPadding(
            bars.left,
            bars.top,
            bars.right,
            if (barVisible) 0 else bars.bottom
        )
        binding.bottomBarContainer.setPadding(bars.left, 0, bars.right, bars.bottom)
    }

    private fun resolveTab(destination: NavDestination): Int? = when (destination.id) {
        R.id.mainFragment, R.id.searchResultsFragment -> R.id.mainFragment
        R.id.favouritesFragment -> R.id.favouritesFragment
        R.id.settingsFragment, R.id.profileFragment -> R.id.settingsFragment
        R.id.carDetailsFragment -> when (navController.previousBackStackEntry?.destination?.id) {
            R.id.favouritesFragment -> R.id.favouritesFragment
            else -> R.id.mainFragment
        }
        else -> null
    }
}
