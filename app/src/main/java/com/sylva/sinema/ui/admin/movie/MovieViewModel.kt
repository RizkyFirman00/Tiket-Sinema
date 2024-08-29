package com.sylva.sinema.ui.admin.movie

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sylva.sinema.model.Movie
import java.util.UUID

class MovieViewModel : ViewModel() {

    private val _movieList = MutableLiveData<List<Movie>>()
    val movieList: LiveData<List<Movie>> get() = _movieList

    private val _movieDetail = MutableLiveData<Movie>()
    val movieDetail: LiveData<Movie> get() = _movieDetail

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> get() = _operationStatus

    private val db = FirebaseFirestore.getInstance()
    private val moviesCollection = db.collection("movies")
    private val storageReference = FirebaseStorage.getInstance().reference

    // Movie Detail Admin Activity
    fun fetchMovies() {
        moviesCollection.get()
            .addOnSuccessListener { result ->
                _movieList.value = result.toObjects(Movie::class.java)
                Log.d("DATA", _movieList.value.toString())
            }
            .addOnFailureListener { exception ->
                logError("fetchMovies", exception)
            }
    }

    fun fetchMovieDetail(movieId: String) {
        moviesCollection.document(movieId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    _movieDetail.value = documentSnapshot.toObject(Movie::class.java)
                } else {
                    logError("fetchMovieDetail", "Document not found")
                }
            }
            .addOnFailureListener { exception ->
                logError("fetchMovieDetail", exception)
            }
    }

    fun updateMovie(
        movieId: String,
        movieName: String,
        description: String,
        rating: String,
        duration: Int,
        newPosterUrl: String?
    ) {
        val movieData = mutableMapOf<String, Any>(
            "movieName" to movieName,
            "description" to description,
            "rating" to rating,
            "duration" to duration,
        )

        newPosterUrl?.let { movieData["posterUrl"] = it }

        moviesCollection.document(movieId)
            .update(movieData)
            .addOnSuccessListener {
                _operationStatus.postValue("Movie updated successfully")
            }
            .addOnFailureListener { exception ->
                logError("updateMovie", exception)
                _operationStatus.postValue("Failed to update movie")
            }
    }

    fun deleteMovie(movieId: String) {
        moviesCollection.document(movieId)
            .delete()
            .addOnSuccessListener {
                _operationStatus.postValue("Movie deleted successfully")
                fetchMovies() // Refresh movie list after deletion
            }
            .addOnFailureListener { exception ->
                logError("deleteMovie", exception)
                _operationStatus.postValue("Failed to delete movie")
            }
    }

    // Movie Add Admin Activity
    fun uploadMovieData(
        movieName: String,
        description: String,
        rating: String,
        duration: Int,
        imageUri: Uri?
    ) {
        if (imageUri == null) {
            _operationStatus.postValue("Image URI is null")
            return
        }

        val posterRef = storageReference.child("posters/${movieName}_${UUID.randomUUID()}")

        posterRef.putFile(imageUri)
            .addOnSuccessListener {
                posterRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveMovieToFirestore(
                        movieName,
                        description,
                        rating,
                        duration,
                        downloadUrl.toString()
                    )
                }
            }
            .addOnFailureListener {
                _operationStatus.postValue("Image upload failed")
            }
    }

    private fun saveMovieToFirestore(
        movieName: String,
        description: String,
        rating: String,
        duration: Int,
        posterUrl: String
    ) {
        val movieData = hashMapOf(
            "movieName" to movieName,
            "description" to description,
            "duration" to duration,
            "rating" to rating,
            "posterUrl" to posterUrl
        )

        val newDocRef = moviesCollection.document()

        newDocRef.set(movieData)
            .addOnSuccessListener {
                newDocRef.update("movieId", newDocRef.id)
                    .addOnSuccessListener {
                        _operationStatus.postValue("Movie added successfully")
                    }
                    .addOnFailureListener { exception ->
                        logError("saveMovieToFirestore", exception)
                        _operationStatus.postValue("Failed to update movieId")
                    }
            }
            .addOnFailureListener { exception ->
                logError("saveMovieToFirestore", exception)
                _operationStatus.postValue("Failed to add movie")
            }
    }

    private fun logError(tag: String, exception: Exception) {
        Log.e("MovieViewModel - $tag", exception.message ?: "Unknown error", exception)
    }

    private fun logError(tag: String, message: String) {
        Log.e("MovieViewModel - $tag", message)
    }
}
