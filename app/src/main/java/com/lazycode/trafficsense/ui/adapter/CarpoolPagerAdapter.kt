package com.lazycode.trafficsense.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lazycode.trafficsense.ui.carpool.fragments.AllCarpoolFragment
import com.lazycode.trafficsense.ui.carpool.fragments.MyCarpoolFragment
import com.lazycode.trafficsense.ui.landing.fragments.FirstFragment

class CarpoolPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = AllCarpoolFragment()
            1 -> fragment = MyCarpoolFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}