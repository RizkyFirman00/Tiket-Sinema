package com.sylva.sinema.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sylva.sinema.databinding.ItemsAdminMoviesBinding
import com.sylva.sinema.databinding.ItemsAdminUsersBinding
import com.sylva.sinema.model.Movie

class AdminMovieAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<Movie, AdminMovieAdapter.HomeAdminViewHolder>(DiffCallback()), Filterable {

    private var movieListFull: List<Movie> = listOf()

    fun sortDataByName() {
        submitList(currentList.sortedBy { it.movieName })
        movieListFull = currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemsAdminMoviesBinding.inflate(inflater, parent, false)
        return HomeAdminViewHolder(binding)
    }

    inner class HomeAdminViewHolder(private val binding: ItemsAdminMoviesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie, onItemClick: (String) -> Unit) {
            binding.movieName.text = movie.movieName
            binding.root.setOnClickListener {
                onItemClick(movie.movieId ?: "")
            }
        }
    }

    override fun onBindViewHolder(holder: HomeAdminViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie, onItemClick)
    }

    private class DiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    movieListFull
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    movieListFull.filter {
                        it.movieName?.lowercase()?.contains(filterPattern) == true
                    }
                }

                return FilterResults().apply {
                    values = filteredList
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<Movie>)
            }
        }
    }
}