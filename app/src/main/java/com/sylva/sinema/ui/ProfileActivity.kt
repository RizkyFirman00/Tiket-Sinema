package com.sylva.sinema.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sylva.sinema.databinding.ActivityProfileBinding
import com.sylva.sinema.utils.Preferences
import com.sylva.sinema.model.User

class ProfileActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private var originalUserData: Map<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Preferences.getUserInfo(this)?.let {
            val email = it.email
            if (email != null) {
                getUserData(email)
            }
        }

        binding.include.btnBack.setOnClickListener { finish() }
        binding.include.btnLogout.setOnClickListener {
            Preferences.logout(this)
            startActivity(Intent(this, LoginActivity::class.java)).let {
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnUpdateData.setOnClickListener {
            val name = binding.edProfileFullName.text.toString()
            val email = binding.edProfileEmail.text.toString()
            val phoneNumber = binding.edProfilePhoneNumber.text.toString()
            val password = binding.edProfilePassword.text.toString()
            val confirmPassword = binding.edProfileConfirmPassword.text.toString()

            if (email.isEmpty() || phoneNumber.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                binding.edProfilePassword.setText("")
                binding.edProfileConfirmPassword.setText("")
                return@setOnClickListener
            }

            originalUserData?.let { originalData ->
                val isDataChanged = originalData["name"] != name ||
                        originalData["email"] != email ||
                        originalData["phoneNumber"] != phoneNumber

                if (!isDataChanged) {
                    Toast.makeText(this, "Tidak ada data yang diubah", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                usersCollection.document(originalData["email"].toString()).get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val storedPassword = documentSnapshot.getString("password")
                        if (storedPassword == password) {
                            if (originalData["email"] != email) {
                                updateUserEmail(originalData["email"].toString(), email, phoneNumber, name, password)
                                finish()
                            } else {
                                updateUserData(originalData["email"].toString(), email, phoneNumber, name, password)
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "Password tidak benar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.addOnFailureListener { exception ->
                    Log.e("ProfileActivity", "Error getting document", exception)
                    Toast.makeText(this, "Gagal memeriksa password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getUserData(email: String) {
        usersCollection.document(email)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    originalUserData = documentSnapshot.data
                    val email = documentSnapshot.getString("email")
                    val phoneNumber = documentSnapshot.getString("phoneNumber")
                    val name = documentSnapshot.getString("name")
                    binding.apply {
                        edProfileEmail.setText(email)
                        edProfilePhoneNumber.setText(phoneNumber)
                        edProfileFullName.setText(name)
                    }
                } else {
                    Log.e("UserData", "Document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserData", "Error getting document", exception)
            }
    }

    private fun updateUserData(
        userId: String,
        email: String,
        phoneNumber: String,
        newUsername: String,
        password: String
    ) {
        val updatedUserData = hashMapOf(
            "email" to email,
            "phoneNumber" to phoneNumber,
            "name" to newUsername,
            "password" to password
        )

        usersCollection.document(userId)
            .update(updatedUserData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Data pengguna berhasil diperbarui", Toast.LENGTH_SHORT).show()
                Preferences.saveUserInfo(
                    User(
                        name = newUsername,
                        email = email,
                        password = password,
                        phoneNumber = phoneNumber
                    ), this
                )
                Preferences.saveEmail(email, this)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal memperbarui data pengguna", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Error updating user data", exception)
            }
    }

    private fun updateUserEmail(
        oldUserId: String,
        newEmail: String,
        phoneNumber: String,
        newUsername: String,
        password: String
    ) {
        val updatedUserData = hashMapOf(
            "email" to newEmail,
            "phoneNumber" to phoneNumber,
            "name" to newUsername,
            "password" to password
        )

        usersCollection.document(newEmail).set(updatedUserData)
            .addOnSuccessListener {
                usersCollection.document(oldUserId).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data pengguna berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        Preferences.saveUserInfo(
                            User(
                                name = newUsername,
                                email = newEmail,
                                password = password,
                                phoneNumber = phoneNumber
                            ), this
                        )
                        Preferences.saveEmail(newEmail, this)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Gagal menghapus data pengguna lama", Toast.LENGTH_SHORT).show()
                        Log.e("ProfileActivity", "Error deleting old user data", exception)
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal memperbarui data pengguna", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Error updating user data", exception)
            }
    }
}
