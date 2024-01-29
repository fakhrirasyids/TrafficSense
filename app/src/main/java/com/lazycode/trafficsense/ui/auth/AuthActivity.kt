package com.lazycode.trafficsense.ui.auth

import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityAuthBinding
import com.lazycode.trafficsense.ui.adapter.LandingPagerAdapter
import com.lazycode.trafficsense.ui.auth.login.LoginFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private var currentPage = 0
    private lateinit var timerJob: Job

    var heightTemp: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setListeners()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.auth_container, LoginFragment())
                .commit()
        }
    }


    private fun setupViewPager() {
        binding.apply {
            viewPager.apply {
                val sectionPagerAdapter = LandingPagerAdapter(this@AuthActivity)
                adapter = sectionPagerAdapter
                val handler = Handler(Looper.getMainLooper())
                val updateRunnable = Runnable {
                    if (currentPage == 3) {
                        currentPage = 0
                    }
                    viewPager.setCurrentItem(currentPage++, true)
                }

                timerJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(DELAY_MS)
                    while (true) {
                        withContext(Dispatchers.Main) {
                            handler.post(updateRunnable)
                        }
                        delay(PERIOD_MS)
                    }
                }
            }

            dotsIndicator.attachTo(binding.viewPager)
        }
    }

    private fun setListeners() {
        binding.apply {
//            btnNext.setOnClickListener {
//                if (viewPager.currentItem < 3) {
//                    viewPager.currentItem = viewPager.currentItem + 1;
//                } else {
//                    finish()
//                    startActivity(Intent(this@LandingActivity, AuthActivity::class.java))
//                }
//            }
//
//            btnBack.setOnClickListener {
//                if (viewPager.currentItem > 0) {
//                    viewPager.currentItem = viewPager.currentItem - 1;
//                }
//            }
        }
    }

    companion object {
        const val DELAY_MS: Long = 500L
        const val PERIOD_MS: Long = 2000L
    }
}