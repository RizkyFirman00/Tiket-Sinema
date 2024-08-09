package com.sylva.sinema.ui.admin.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sylva.sinema.ui.admin.user.detail.UserDetailFragment
import com.sylva.sinema.ui.admin.user.order.UserOrderFragment

class UserDetailPagerAdapter(
    activity: FragmentActivity,
    private val userEmail: String?
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("User Email", userEmail)
                }
            }
            1 -> UserOrderFragment()
            else -> throw IllegalStateException("Invalid fragment index")
        }
    }
}
