package com.sylva.sinema.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sylva.sinema.databinding.ActivityUserDetailAdminBinding
import com.sylva.sinema.model.User
import com.sylva.sinema.utils.Preferences

class UserDetailAdminActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
    private var originalUserData: Map<String, Any>? = null
    private val binding by lazy { ActivityUserDetailAdminBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.include.btnBack.setOnClickListener {
            finish()
        }

        val userEmail = intent.getStringExtra("User Email")
        if (userEmail != null) {
            getUserData(userEmail)
        }

        binding.btnUpdateData.setOnClickListener {
            val name = binding.edProfileFullName.text.toString()
            val email = binding.edProfileEmail.text.toString()
            val phoneNumber = binding.edProfilePhoneNumber.text.toString()
            val password = binding.edProfilePassword.text.toString()

            if (email.isEmpty() || phoneNumber.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show()
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

                loadingProgress()

                if (originalData["email"] != email) {
                    updateUserEmail(originalData["email"].toString(), email, phoneNumber, name, password)
                } else {
                    updateUserData(originalData["email"].toString(), email, phoneNumber, name, password)
                }
            }
        }

        binding.btnDeleteData.setOnClickListener {
            val email = binding.edProfileEmail.text.toString()
            if (email.isNotEmpty()) {
                deleteUserData(email)
            } else {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserData(email: String) {
        loadingProgress()
        usersCollection.document(email)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    originalUserData = documentSnapshot.data
                    val email = documentSnapshot.getString("email")
                    val phoneNumber = documentSnapshot.getString("phoneNumber")
                    val name = documentSnapshot.getString("name")
                    val password = documentSnapshot.getString("password")
                    binding.apply {
                        edProfileEmail.setText(email)
                        edProfilePhoneNumber.setText(phoneNumber)
                        edProfileFullName.setText(name)
                        edProfilePassword.setText(password)
                    }
                } else {
                    Log.e("UserData", "Document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserData", "Error getting document", exception)
            }
            .addOnCompleteListener {
                unLoadingProgress()
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
            .addOnCompleteListener {
                unLoadingProgress()
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
                    .addOnCompleteListener {
                        unLoadingProgress()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal memperbarui data pengguna", Toast.LENGTH_SHORT).show()
                Log.e("ProfileActivity", "Error updating user data", exception)
            }
            .addOnCompleteListener {
                unLoadingProgress()
            }
    }

    private fun deleteUserData(email: String) {
        loadingProgress()
        usersCollection.document(email)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Data pengguna berhasil dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal menghapus data pengguna", Toast.LENGTH_SHORT).show()
                Log.e("UserDetailActivity", "Error deleting user data", exception)
            }
            .addOnCompleteListener {
                unLoadingProgress()
            }
    }

    private fun loadingProgress() {
        binding.apply {
            progressCircular.visibility = View.VISIBLE
            edProfileFullName.isEnabled = false
            edProfilePhoneNumber.isEnabled = false
            edProfileEmail.isEnabled = false
            edProfilePassword.isEnabled = false
            btnUpdateData.isEnabled = false
            btnDeleteData.isEnabled = false
        }
    }

    private fun unLoadingProgress() {
        binding.apply {
            progressCircular.visibility = View.GONE
            edProfileFullName.isEnabled = true
            edProfilePhoneNumber.isEnabled = true
            edProfileEmail.isEnabled = true
            edProfilePassword.isEnabled = true
            btnDeleteData.isEnabled = true
            btnUpdateData.isEnabled = true
        }
    }
}
