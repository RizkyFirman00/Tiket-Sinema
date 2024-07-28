package com.sylva.sinema.ui.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sylva.sinema.R
import com.sylva.sinema.databinding.ActivityHomeAdminBinding

class HomeAdminActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeAdminBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}