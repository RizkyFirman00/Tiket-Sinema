package com.sylva.sinema.ui.admin.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sylva.sinema.adapter.admin.AdminMovieAdapter
import com.sylva.sinema.databinding.FragmentMoviesBinding

class MoviesFragment : Fragment() {

    private val viewModel: MovieViewModel by viewModels()
    private lateinit var adminMovieAdapter: AdminMovieAdapter

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                viewModel.fetchMovies()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMoviesBinding.inflate(inflater, container, false)

        adminMovieAdapter = AdminMovieAdapter { movieId ->
            navigateToDetailDataActivity(movieId)
        }
        binding.rvMovie.adapter = adminMovieAdapter
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext())

        // Observe LiveData from ViewModel
        viewModel.movieList.observe(viewLifecycleOwner) { movies ->
            adminMovieAdapter.submitList(movies)
        }

        // Fetch movies initially
        viewModel.fetchMovies()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    adminMovieAdapter.filter.filter(newText)
                } else {
                    viewModel.movieList.value?.let { adminMovieAdapter.submitList(it) }
                }
                return true
            }
        })

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
            binding.searchBar.requestFocusFromTouch()
        }

        binding.addData.setOnClickListener {
            getResult.launch(Intent(requireContext(), MovieAddAdminActivity::class.java))
        }

        return binding.root
    }

    private fun navigateToDetailDataActivity(movieId: String) {
        val intent = Intent(requireContext(), MovieDetailAdminActivity::class.java)
        intent.putExtra("Movie ID", movieId)
        getResult.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMovies()
    }
}

