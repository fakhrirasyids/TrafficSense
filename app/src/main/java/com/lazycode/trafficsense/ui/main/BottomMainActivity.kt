package com.lazycode.trafficsense.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityBottomMainBinding
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.ui.splash.SplashActivity
import com.lazycode.trafficsense.utils.Constants.alertDialogMessage
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class BottomMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityBottomMainBinding
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_main)
        navView.setupWithNavController(navController)

        setToolbar()

        binding.navView.setOnItemSelectedListener { id ->
            if (id.toString() == "Home") {
                navController.navigate(R.id.navigation_home)
            } else if (id.toString() == "Scan") {
                navController.navigate(R.id.navigation_scan)
            }
            true
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            inflateMenu(R.menu.main_menu)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_settings) {
                    startActivity(
                        Intent(
                            this@BottomMainActivity,
                            ProfileSettingsActivity::class.java
                        )
                    )
                }
                true
            }
        }
    }
}