package com.sylva.sinema.ui.admin.movie

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.sylva.sinema.databinding.ActivityMovieDetailAdminBinding

class MovieDetailAdminActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val movieCollection = db.collection("movies")

    private var originalMovieData: Map<String, Any>? = null
    private val binding by lazy { ActivityMovieDetailAdminBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val movieId = intent.getStringExtra("Movie ID")
        if (movieId != null) {
            getMovieData(movieId)
        }


        binding.toolbarMovie.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpdateData.setOnClickListener {

        }

        binding.btnDeleteData.setOnClickListener {
            movieId?.let { id ->
                showDeleteConfirmationDialog(id)
            }
        }
    }

    private fun getMovieData(movieName: String) {
        loadingProgress()
        movieCollection.document(movieName)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    originalMovieData = documentSnapshot.data
                    val movieName = documentSnapshot.getString("movieName")
                    val description = documentSnapshot.getString("description")
                    val rating = documentSnapshot.getString("rating")
                    val posterUrl = documentSnapshot.getString("posterUrl")
                    binding.apply {
                        edMovieMovieName.setText(movieName)
                        edMovieRating.setText(rating)
                        edMovieDescription.setText(description)
                        Glide.with(this@MovieDetailAdminActivity)
                            .load(posterUrl?.toUri())
                            .into(binding.ivImage)
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

    private fun showDeleteConfirmationDialog(movieId: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Delete Movie")
            setMessage("Are you sure you want to delete this movie?")
            setPositiveButton("Yes") { _, _ ->
                deleteMovie(movieId)
            }
            setNegativeButton("No", null)
        }.show()
    }

    private fun deleteMovie(movieId: String) {
        loadingProgress()
        movieCollection.document(movieId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Movie deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e("MovieDetailAdmin", "Error deleting document", exception)
                Toast.makeText(this, "Failed to delete movie", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                unLoadingProgress()
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