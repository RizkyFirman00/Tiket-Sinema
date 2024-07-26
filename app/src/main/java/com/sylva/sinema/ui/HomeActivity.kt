package com.sylva.sinema.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sylva.sinema.databinding.ActivityHomeBinding
import com.sylva.sinema.utils.Preferences

class HomeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)).let {
                Preferences.logout(this)
                finish()
            }

        }
        Preferences.getUserInfo(this).let {
            binding.apply {
                if (it != null) {
                    userName.text = it.name
                    userEmail.text = it.email
                    userPassword.text = it.password
                    userPhoneNumber.text = it.phoneNumber
                }
            }
        }
    }
}