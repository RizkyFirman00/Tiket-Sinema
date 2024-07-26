package com.sylva.sinema.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sylva.sinema.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)).let {
                finish()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString()
            val phoneNumber = binding.edRegisterPhoneNumber.text.toString()
            val name = binding.edRegisterFullName.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString()

            when {
                (email.isEmpty() || phoneNumber.isEmpty() || name.isEmpty() || password.isEmpty()) -> {
                    Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    registerUser(name, phoneNumber, email, password)
                }
            }
        }

    }

    private fun registerUser(name: String, phoneNumber: String, email: String, password: String) {
        val user = hashMapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "email" to email,
            "password" to password,
        )
        usersCollection.whereEqualTo("email", email).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val existingUser = task.result?.documents
                if (!existingUser.isNullOrEmpty()) {
                    Toast.makeText(this, "Username sudah dipakai", Toast.LENGTH_SHORT).show()
                } else {
                    usersCollection.document(email).set(user)
                        .addOnCompleteListener { registrationTask ->
                            if (registrationTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Registrasi berhasil",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                ).also {
                                    startActivity(it)
                                    finish()
                                }
                            } else {
                                Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Error checking existing user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadingProgress() {
        binding.apply {
//            progressBar2.visibility = android.view.View.VISIBLE
            edRegisterFullName.isEnabled = false
            edRegisterPhoneNumber.isEnabled = false
            edRegisterEmail.isEnabled = false
            edRegisterPassword.isEnabled = false
            btnRegister.isEnabled = false
        }
    }

    private fun unLoadingProgress() {
        binding.apply {
//            progressBar2.visibility = android.view.View.GONE
            edRegisterFullName.isEnabled = true
            edRegisterPhoneNumber.isEnabled = true
            edRegisterEmail.isEnabled = true
            edRegisterPassword.isEnabled = true
            btnRegister.isEnabled = true
        }
    }
}