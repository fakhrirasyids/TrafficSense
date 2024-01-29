package com.lazycode.trafficsense.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lazycode.trafficsense.ui.landing.fragments.FirstFragment
import com.lazycode.trafficsense.ui.landing.fragments.FourthFragment
import com.lazycode.trafficsense.ui.landing.fragments.SecondFragment
import com.lazycode.trafficsense.ui.landing.fragments.ThirdFragment
import com.lazycode.trafficsense.ui.main.ui.home.fragments.DuaFragment
import com.lazycode.trafficsense.ui.main.ui.home.fragments.SatuFragment
import com.lazycode.trafficsense.ui.main.ui.home.fragments.TigaFragment

class HomePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = SatuFragment()
            1 -> fragment = DuaFragment()
            2 -> fragment = TigaFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 3
    }
}