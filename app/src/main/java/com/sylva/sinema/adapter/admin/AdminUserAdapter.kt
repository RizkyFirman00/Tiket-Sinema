package com.sylva.sinema.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sylva.sinema.databinding.ItemsAdminUsersBinding
import com.sylva.sinema.model.User

class AdminUserAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<User, AdminUserAdapter.HomeAdminViewHolder>(DiffCallback()), Filterable {

    private var userListFull: List<User> = listOf()

    fun sortDataByName() {
        submitList(currentList.sortedBy { it.email })
        userListFull = currentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemsAdminUsersBinding.inflate(inflater, parent, false)
        return HomeAdminViewHolder(binding)
    }

    inner class HomeAdminViewHolder(private val binding: ItemsAdminUsersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, onItemClick: (String) -> Unit) {
            binding.emailUser.text = user.email
            binding.root.setOnClickListener {
                onItemClick(user.email ?: "")
            }
        }
    }

    override fun onBindViewHolder(holder: HomeAdminViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, onItemClick)
    }

    private class DiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    userListFull
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    userListFull.filter {
                        it.email?.lowercase()?.contains(filterPattern) == true
                    }
                }

                return FilterResults().apply {
                    values = filteredList
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<User>)
            }
        }
    }
}
