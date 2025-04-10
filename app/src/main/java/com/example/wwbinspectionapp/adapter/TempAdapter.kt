// TempAdapter.kt
package com.example.wwbinspectionapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wwbinspectionapp.ui.fragment.DeathFragment
import com.example.wwbinspectionapp.ui.fragment.HajjGrantsFragment
import com.example.wwbinspectionapp.ui.fragment.MarrigeGrantsFragment
import com.example.wwbinspectionapp.ui.fragment.ScholerShipFragment

class TempAdapter(fragmentActivity: FragmentActivity, private val tabCount: Int) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = tabCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MarrigeGrantsFragment()
            1 -> DeathFragment()
            2 -> HajjGrantsFragment()
            3 -> ScholerShipFragment()
            else -> MarrigeGrantsFragment()
        }
    }
}
