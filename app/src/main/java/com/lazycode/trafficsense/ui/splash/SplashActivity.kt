package com.lazycode.trafficsense.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivitySplashBinding
import com.lazycode.trafficsense.ui.auth.AuthActivity
import com.lazycode.trafficsense.ui.landing.LandingActivity
import com.lazycode.trafficsense.ui.main.BottomMainActivity
import com.lazycode.trafficsense.utils.UserPreferences.Companion.preferenceDefaultValue
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private val splashViewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            splashViewModel.getAccessToken().observe(this) { accessToken ->
                if (accessToken == preferenceDefaultValue) {
                    finish()
                    startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                } else {
                    finish()
                    startActivity(Intent(this@SplashActivity, BottomMainActivity::class.java))
                }
            }
        }, 1500L)
    }
}