package com.lazycode.trafficsense.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.ActivityLandingBinding
import com.lazycode.trafficsense.ui.adapter.LandingPagerAdapter
import com.lazycode.trafficsense.ui.auth.AuthActivity


class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setListeners()
    }

    private fun setupViewPager() {

        binding.apply {
            viewPager.apply {
                val sectionPagerAdapter = LandingPagerAdapter(this@LandingActivity)
                adapter = sectionPagerAdapter
                registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        btnBack.isVisible = position != 0

                        if (position == 3) {
                            btnNext.text = StringBuilder("Selesai")
                        } else {
                            btnNext.text = StringBuilder("Lanjut")
                        }
                    }
                })
            }

            dotsIndicator.attachTo(binding.viewPager)
        }
    }

    private fun setListeners() {
        binding.apply {
            btnNext.setOnClickListener {
                if (viewPager.currentItem < 3) {
                    viewPager.currentItem = viewPager.currentItem + 1;
                } else {
                    finish()
                    startActivity(Intent(this@LandingActivity, AuthActivity::class.java))
                }
            }

            btnBack.setOnClickListener {
                if (viewPager.currentItem > 0) {
                    viewPager.currentItem = viewPager.currentItem - 1;
                }
            }
        }
    }
}