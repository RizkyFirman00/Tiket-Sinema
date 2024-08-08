package com.sylva.sinema.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sylva.sinema.databinding.ActivityLoginBinding
import com.sylva.sinema.model.User
import com.sylva.sinema.ui.admin.HomeAdminActivity
import com.sylva.sinema.ui.user.HomeUserActivity
import com.sylva.sinema.utils.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Preferences.getUserInfo(this)?.let {
            Log.d("User Info Login", "$it")
        }

        val usernameCheck = Preferences.getEmail(this)
        if (Preferences.checkEmail(this) && usernameCheck == "sylva") {
            Intent(this, HomeAdminActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else if (Preferences.checkEmail(this)) {
            Intent(this, HomeUserActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email == "sylva" && password == "flowrends") {
                loadingProgress()
                Preferences.saveEmail(email, this)
                Intent(this, HomeAdminActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            } else {
                loadingProgress()
                CoroutineScope(Dispatchers.Main).launch {
                    val loginResult = loginUser(email, password)
                    handleLoginResult(loginResult)
                }
            }
        }

        binding.btnToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java)).let {
                finish()
            }
        }
    }

    private fun handleLoginResult(result: Pair<Boolean, String?>) {
        if (result.first) {
            val username = Preferences.getEmail(this)
            Toast.makeText(this, "Selamat Datang $username", Toast.LENGTH_SHORT).show()
            Intent(this, HomeUserActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else {
            Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loginUser(email: String, password: String): Pair<Boolean, String?> {
        return try {
            loadingProgress()
            val userQuery = usersCollection.whereEqualTo("email", email).get().await()

            if (!userQuery.isEmpty) {
                val userDoc = userQuery.documents[0]

                val storedPassword = userDoc.getString("password")
                if (storedPassword == password) {
                    val name = userDoc.getString("name")
                    val password = userDoc.getString("password")
                    val phoneNumber = userDoc.getString("phoneNumber")
                    Preferences.saveUserInfo(
                        User(
                            name = name ?: "",
                            email = email ?: "",
                            password = password,
                            phoneNumber = phoneNumber ?: "",
                        ), this
                    )
                    unLoadingProgress()
                    Log.d("USER ID LOGIN", email)
                    if (name != null) {
                        Preferences.saveEmail(email, this)
                    }
                    Preferences.saveEmail(email, this)
                    Pair(true, email)
                } else {
                    unLoadingProgress()
                    Pair(false, null)
                }
            } else {
                unLoadingProgress()
                Pair(false, null)
            }
        } catch (e: Exception) {
            unLoadingProgress()
            Pair(false, null)
        }
    }

    private fun loadingProgress() {
        binding.apply {
            progressCircular.visibility = android.view.View.VISIBLE
            edLoginEmail.isEnabled = false
            edLoginPassword.isEnabled = false
            btnLogin.isEnabled = false
            btnToRegister.isEnabled = false
        }
    }

    private fun unLoadingProgress() {
        binding.apply {
            progressCircular.visibility = android.view.View.GONE
            edLoginEmail.isEnabled = true
            edLoginPassword.isEnabled = true
            btnLogin.isEnabled = true
            btnToRegister.isEnabled = true
        }
    }
}