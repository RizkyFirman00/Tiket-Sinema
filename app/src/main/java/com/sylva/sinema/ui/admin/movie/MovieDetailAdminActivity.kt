package com.sylva.sinema.ui.admin.movie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.sylva.sinema.R
import com.sylva.sinema.databinding.ActivityMovieDetailAdminBinding
import java.io.ByteArrayOutputStream
import java.util.UUID

class MovieDetailAdminActivity : AppCompatActivity() {
    private val movieViewModel: MovieViewModel by viewModels()
    private val binding by lazy { ActivityMovieDetailAdminBinding.inflate(layoutInflater) }

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("Movie ID")
        movieId?.let {
            movieViewModel.fetchMovieDetail(it)
        }

        binding.toolbarMovie.btnBack.setOnClickListener {
            finish()
        }

        binding.btnReplacePoster.setOnClickListener {
            startGallery()
        }

        binding.btnUpdateData.setOnClickListener {
            val movieName = binding.edMovieMovieName.text.toString().trim()
            val description = binding.edMovieDescription.text.toString().trim()
            val rating = binding.edMovieRating.text.toString().trim()
            val duration = binding.edMovieDuration.text.toString().toInt()

            if (movieId != null) {
                loadingProgress()  // Show loading indicator
                if (selectedImageUri != null) {
                    uploadPosterAndSaveMovie(
                        movieId,
                        movieName,
                        description,
                        rating,
                        duration,
                        selectedImageUri!!
                    )
                } else {
                    movieViewModel.updateMovie(movieId, movieName, description, rating, duration, null)
                }
            }
        }


        binding.btnDeleteData.setOnClickListener {
            movieId?.let { id ->
                showDeleteConfirmationDialog(id)
            }
        }

        movieViewModel.movieDetail.observe(this) { movie ->
            movie?.let {
                binding.apply {
                    edMovieMovieName.setText(it.movieName)
                    edMovieRating.setText(it.rating)
                    edMovieDescription.setText(it.description)
                    edMovieDuration.setText(it.movieDuration.toString())
                    Glide.with(this@MovieDetailAdminActivity)
                        .load(it.posterUrl?.toUri())
                        .into(binding.ivImage)
                }
            }
        }

        movieViewModel.operationStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
            if (status.contains("successfully")) {
                setResult(RESULT_OK) // Inform fragment to refresh data
                finish()
            }
        }
    }

    private fun showDeleteConfirmationDialog(movieId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Delete Movie")
            setMessage("Are you sure you want to delete this movie?")
            setPositiveButton("Yes") { _, _ ->
                movieViewModel.deleteMovie(movieId)
            }
            setNegativeButton("No", null)
        }.show()
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

    private fun uploadPosterAndSaveMovie(
        movieId: String,
        movieName: String,
        description: String,
        rating: String,
        duration: Int,
        newImageUri: Uri
    ) {
        movieViewModel.movieDetail.observe(this) { movie ->
            val oldPosterUrl = movie?.posterUrl

            oldPosterUrl?.let { url ->
                val oldPosterRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                oldPosterRef.delete().addOnSuccessListener {
                    Log.d("MovieDetailAdmin", "Old poster deleted successfully")

                    uploadNewPosterAndSave(movieId, movieName, description, rating, duration, newImageUri)
                }.addOnFailureListener {
                    Log.e("MovieDetailAdmin", "Failed to delete old poster", it)
                    unLoadingProgress()
                    Toast.makeText(this, "Failed to delete old poster", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                uploadNewPosterAndSave(movieId, movieName, description, rating, duration, newImageUri)
            }
        }
    }

    private fun uploadNewPosterAndSave(
        movieId: String,
        movieName: String,
        description: String,
        rating: String,
        duration: Int,
        newImageUri: Uri
    ) {
        val posterRef =
            FirebaseStorage.getInstance().reference.child("posters/${movieName}_${UUID.randomUUID()}")

        posterRef.putFile(newImageUri)
            .addOnSuccessListener {
                posterRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // After successful upload, update the movie data with the new poster URL
                    movieViewModel.updateMovie(
                        movieId,
                        movieName,
                        description,
                        rating,
                        duration,
                        downloadUrl.toString()
                    )
                }.addOnFailureListener {
                    unLoadingProgress() // Hide loading indicator
                    Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                unLoadingProgress() // Hide loading indicator
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadingProgress() {
        binding.apply {
            progressCircular.visibility = View.VISIBLE
            edMovieMovieName.isEnabled = false
            edMovieRating.isEnabled = false
            edMovieDescription.isEnabled = false
            btnUpdateData.isEnabled = false
            btnDeleteData.isEnabled = false
        }
    }

    private fun unLoadingProgress() {
        binding.apply {
            progressCircular.visibility = View.GONE
            edMovieMovieName.isEnabled = true
            edMovieRating.isEnabled = true
            edMovieDescription.isEnabled = true
            btnDeleteData.isEnabled = true
            btnUpdateData.isEnabled = true
        }
    }

}
