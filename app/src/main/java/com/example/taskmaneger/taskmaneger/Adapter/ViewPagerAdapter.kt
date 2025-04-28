package com.example.taskmaneger.taskmaneger.Adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.taskmaneger.ui.CompletedItemsFragment
import com.example.taskmaneger.ui.PendingItemsFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingItemsFragment()
            1 -> CompletedItemsFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
