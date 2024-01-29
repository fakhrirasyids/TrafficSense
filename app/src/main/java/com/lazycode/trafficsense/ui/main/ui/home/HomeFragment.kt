package com.lazycode.trafficsense.ui.main.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lazycode.trafficsense.R
import com.lazycode.trafficsense.databinding.FragmentHomeBinding
import com.lazycode.trafficsense.ui.adapter.HomePagerAdapter
import com.lazycode.trafficsense.ui.auth.AuthActivity
import com.lazycode.trafficsense.ui.carpool.CarpoolActivity
import com.lazycode.trafficsense.ui.dynamicroute.DynamicRouteActivity
import com.lazycode.trafficsense.ui.main.BottomMainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentPage = 0
    private lateinit var timerJob: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupViewPager()
        setListeners()

        return binding.root
    }

    private fun setupViewPager() {
        binding.apply {
            viewPager.apply {
                val homePagerAdapter = HomePagerAdapter(requireActivity() as AppCompatActivity)
                adapter = homePagerAdapter
                val handler = Handler(Looper.getMainLooper())
                val updateRunnable = Runnable {
                    if (currentPage == 3) {
                        currentPage = 0
                    }
                    viewPager.setCurrentItem(currentPage++, true)
                }

                timerJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(AuthActivity.DELAY_MS)
                    while (true) {
                        withContext(Dispatchers.Main) {
                            handler.post(updateRunnable)
                        }
                        delay(AuthActivity.PERIOD_MS)
                    }
                }
            }

            dotsIndicator.attachTo(binding.viewPager)
        }
    }

    private fun setListeners() {
        binding.apply {
            btnCarpooling.setOnClickListener {
                val iCarpooling = Intent(requireActivity(), CarpoolActivity::class.java)
                startActivity(iCarpooling)
            }

            btnDynamicRouting.setOnClickListener {
                val iDynamicRouting = Intent(requireActivity(), DynamicRouteActivity::class.java)
                startActivity(iDynamicRouting)
            }

            btnPolutionMonitoring.setOnClickListener {
                val navController = (requireActivity() as BottomMainActivity).findNavController(R.id.nav_host_fragment_activity_bottom_main)
                navController.navigate(R.id.navigation_scan)
            }
        }
    }

    companion object {
        const val DELAY_MS: Long = 500L
        const val PERIOD_MS: Long = 2000L
    }
}