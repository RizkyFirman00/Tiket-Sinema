package com.sylva.sinema.model

data class Studio(
    val studioId: String,
    val studioName: String,
    val studioSeats: List<Seat>
)

data class Seat(
    val seatId: String,
    val seatNumber: String,
    val isBooked: Boolean
)