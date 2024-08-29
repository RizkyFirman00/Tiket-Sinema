package com.sylva.sinema.ui.admin.movie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sylva.sinema.R
import com.sylva.sinema.databinding.ActivityMovieAddAdminBinding
import java.util.UUID

class MovieAddAdminActivity : AppCompatActivity() {
    private var selectedImageUri: Uri? = null
    private val binding by lazy { ActivityMovieAddAdminBinding.inflate(layoutInflater) }
    private val movieViewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.include.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddPoster.setOnClickListener {
            startGallery()
        }

        binding.btnAddMovie.setOnClickListener {
            val movieName = binding.edMovieName.text.toString().trim()
            val description = binding.edDesc.text.toString().trim()
            val selectedRating = binding.edRating.text.toString()
            val duration = binding.edDuration.text.toString().toInt()

            if (movieName.isEmpty() || description.isEmpty() || selectedRating.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadingProgress()
            movieViewModel.uploadMovieData(
                movieName,
                description,
                selectedRating,
                duration,
                selectedImageUri
            )
        }

        // Observe the ViewModel status
        movieViewModel.operationStatus.observe(this) { status ->
            unLoadingProgress()
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            if (status == "Movie added successfully") {
                finish() // Close activity after successful addition
            }
        }
    }

    // Permission Function
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 123
        private const val REQUEST_CODE_GALLERY = 789
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    // Gallery Function
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        startActivityForResult(chooser, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(binding.ivImage)
            }
        }
    }


    private fun loadingProgress() {
        binding.apply {
            progressCircular.visibility = android.view.View.VISIBLE
            edMovieName.isEnabled = false
            edDesc.isEnabled = false
            edRating.isEnabled = false
            btnAddMovie.isEnabled = false
            btnAddPoster.isEnabled = false
        }
    }

    private fun unLoadingProgress() {
        binding.apply {
            progressCircular.visibility = android.view.View.GONE
            edMovieName.isEnabled = true
            edDesc.isEnabled = true
            edRating.isEnabled = true
            btnAddMovie.isEnabled = true
            btnAddPoster.isEnabled = true
        }
    }
}
