package com.sylva.sinema.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sylva.sinema.R
import com.sylva.sinema.databinding.ActivityHomeBinding
import com.sylva.sinema.utils.Preferences

class HomeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.include.btnToProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.movies -> {
//                    startActivity(Intent(this, MoviesActivity::class.java))
                    true
                }
                R.id.cinemas -> {
//                    startActivity(Intent(this, CinemasActivity::class.java))
                    true
                }
                R.id.ticket -> {
//                    startActivity(Intent(this, TicketActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}