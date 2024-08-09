package com.sylva.sinema.ui.admin.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sylva.sinema.adapter.admin.AdminMovieAdapter
import com.sylva.sinema.databinding.FragmentMoviesBinding
import com.sylva.sinema.model.Movie

class MoviesFragment : Fragment() {
    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private val moviesCollection = db.collection("movies")

    private lateinit var adminMovieAdapter: AdminMovieAdapter
    private var movieList = mutableListOf<Movie>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)

        adminMovieAdapter = AdminMovieAdapter {
            navigateToDetailDataActivity(it)
        }
        binding.rvMovie.adapter = adminMovieAdapter
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext())
        fetchDataAndUpdateRecyclerView()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    adminMovieAdapter.filter.filter(newText)
                } else {
                    adminMovieAdapter.submitList(movieList)
                }
                return true
            }
        })

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
            binding.searchBar.requestFocusFromTouch()
        }

        binding.addData.setOnClickListener {
            startActivity(Intent(requireContext(), MovieAddAdminActivity::class.java))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchDataAndUpdateRecyclerView()
    }

    private fun fetchDataAndUpdateRecyclerView() {
        movieList.clear()
        moviesCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cakeData = document.toObject(Movie::class.java)
                    movieList.add(cakeData)
                }
                adminMovieAdapter.submitList(movieList.toList())
                adminMovieAdapter.sortDataByName()
                Log.d("AdminActivity", "Fetched data: $movieList")
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "Error fetching data", exception)
            }
    }

    private fun navigateToDetailDataActivity(movieName: String) {
        val intent = Intent(requireContext(), MovieDetailAdminActivity::class.java)
        intent.putExtra("User Email", movieName)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}