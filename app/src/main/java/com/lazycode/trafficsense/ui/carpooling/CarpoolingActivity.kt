package com.lazycode.trafficsense.ui.carpooling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityCarpoolingBinding
import com.lazycode.trafficsense.ui.profilesettings.ProfileSettingsActivity
import com.lazycode.trafficsense.ui.splash.SplashActivity
import com.lazycode.trafficsense.utils.Constants
import com.lazycode.trafficsense.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CarpoolingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCarpoolingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarpoolingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            inflateMenu(R.menu.carpooling_menu)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_settings) {
                    val iSettings =
                        Intent(this@CarpoolingActivity, ProfileSettingsActivity::class.java)
                    startActivity(iSettings)
                }
                true
            }

            setNavigationOnClickListener { finish() }
        }
        setListeners()
    }

    private fun setListeners() {
        binding.apply {

        }
    }
}