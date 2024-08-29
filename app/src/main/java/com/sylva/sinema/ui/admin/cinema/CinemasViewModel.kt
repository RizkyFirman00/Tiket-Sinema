package com.sylva.sinema.ui.admin.cinema

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.sylva.sinema.model.Cinema

class CinemasViewModel : ViewModel() {
    private val _cinemaList = MutableLiveData<List<Cinema>>()
    val cinemaList: LiveData<List<Cinema>> get() = _cinemaList

    private val _cinemaDetail = MutableLiveData<Cinema>()
    val cinemaDetail: LiveData<Cinema> get() = _cinemaDetail

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> get() = _operationStatus

    private val db = Firebase.firestore
    private val cinemasCollection = db.collection("cinemas")

    private val firestore = FirebaseFirestore.getInstance()



}
