package com.example.wwbinspectionapp.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wwbinspectionapp.ui.fragment.DeathFragment
import com.example.wwbinspectionapp.ui.fragment.HajjGrantsFragment
import com.example.wwbinspectionapp.ui.fragment.MarrigeGrantsFragment
import com.example.wwbinspectionapp.ui.fragment.ScholerShipFragment

class TempAdapter(
    fragmentActivity: FragmentActivity,
    private val totalTabs: Int,
    private val factoryId: Int  // Add factoryId as a parameter
) : FragmentStateAdapter(fragmentActivity) {

    // this is for fragment tabs
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MarrigeGrantsFragment.newInstance(factoryId)
            1 -> DeathFragment.newInstance(factoryId)
            2 -> HajjGrantsFragment.newInstance(factoryId)
            3 -> ScholerShipFragment.newInstance(factoryId)
            else -> MarrigeGrantsFragment.newInstance(factoryId)
        }
    }

    override fun getItemCount(): Int {
        return totalTabs
    }
}