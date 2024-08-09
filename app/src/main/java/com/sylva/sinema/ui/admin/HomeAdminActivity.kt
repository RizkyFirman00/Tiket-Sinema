package com.sylva.sinema.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sylva.sinema.utils.Preferences
import androidx.fragment.app.Fragment
import com.sylva.sinema.R
import com.sylva.sinema.databinding.ActivityHomeAdminBinding
import com.sylva.sinema.ui.LoginActivity
import com.sylva.sinema.ui.admin.user.UserFragment

class HomeAdminActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeAdminBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(UserFragment())
        }

        binding.include.btnLogout.setOnClickListener {
            Preferences.logout(this)
            startActivity(Intent(this, LoginActivity::class.java)).let {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.navbar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.users -> {
                    replaceFragment(UserFragment())
                    true
                }

                R.id.movies -> {
//                    startActivity(Intent(this, MoviesActivity::class.java))
                    true
                }

                R.id.cinemas -> {
//                    startActivity(Intent(this, CinemasActivity::class.java))
                    true
                }

                R.id.orders -> {
//                    startActivity(Intent(this, TicketActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}