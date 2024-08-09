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
import com.sylva.sinema.ui.admin.cinema.CinemasFragment
import com.sylva.sinema.ui.admin.movie.MoviesFragment
import com.sylva.sinema.ui.admin.order.OrderFragment
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
                    replaceFragment(MoviesFragment())
                    true
                }

                R.id.cinemas -> {
                    replaceFragment(CinemasFragment())
                    true
                }

                R.id.orders -> {
                    replaceFragment(OrderFragment())
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