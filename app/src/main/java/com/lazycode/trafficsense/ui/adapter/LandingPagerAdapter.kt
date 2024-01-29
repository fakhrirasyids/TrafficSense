package com.lazycode.trafficsense.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lazycode.trafficsense.ui.landing.fragments.FirstFragment
import com.lazycode.trafficsense.ui.landing.fragments.FourthFragment
import com.lazycode.trafficsense.ui.landing.fragments.SecondFragment
import com.lazycode.trafficsense.ui.landing.fragments.ThirdFragment

class LandingPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FourthFragment()
            1 -> fragment = FirstFragment()
            2 -> fragment = SecondFragment()
            3 -> fragment = ThirdFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 4
    }
}