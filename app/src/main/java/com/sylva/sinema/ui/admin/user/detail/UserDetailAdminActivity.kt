package com.sylva.sinema.ui.admin.user.detail

import UserDetailPagerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.sylva.sinema.R
import com.sylva.sinema.databinding.ActivityUserDetailAdminBinding

class UserDetailAdminActivity : AppCompatActivity() {

    private val binding by lazy { ActivityUserDetailAdminBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.include.btnBack.setOnClickListener {
            finish()
        }

        val userEmail = intent.getStringExtra("User Email")

        val pagerAdapter = UserDetailPagerAdapter(this, userEmail)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "User Detail"
                1 -> "User Orders"
                else -> null
            }
        }.attach()
    }
}
