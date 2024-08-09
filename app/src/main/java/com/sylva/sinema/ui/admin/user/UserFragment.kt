package com.sylva.sinema.ui.admin.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sylva.sinema.adapter.admin.AdminUserAdapter
import com.sylva.sinema.databinding.FragmentUserBinding
import com.sylva.sinema.model.User

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    private lateinit var adminUserAdapter: AdminUserAdapter
    private var userList = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)

        adminUserAdapter = AdminUserAdapter {
            navigateToDetailDataActivity(it)
        }
        binding.rvUser.adapter = adminUserAdapter
        binding.rvUser.layoutManager = LinearLayoutManager(requireContext())
        fetchDataAndUpdateRecyclerView()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    adminUserAdapter.filter.filter(newText)
                } else {
                    adminUserAdapter.submitList(userList)
                }
                return true
            }
        })

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
            binding.searchBar.requestFocusFromTouch()
        }

        return binding.root
    }

    private fun fetchDataAndUpdateRecyclerView() {
        usersCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val cakeData = document.toObject(User::class.java)
                    userList.add(cakeData)
                }
                adminUserAdapter.submitList(userList)
                adminUserAdapter.sortDataByName()
                Log.d("AdminActivity", "Fetched data: $userList")
            }
            .addOnFailureListener { exception ->
                Log.e("AdminActivity", "Error fetching data", exception)
            }
    }

    private fun navigateToDetailDataActivity(userEmail: String) {
        val intent = Intent(requireContext(), UserDetailAdminActivity::class.java)
        intent.putExtra("User Email", userEmail)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}