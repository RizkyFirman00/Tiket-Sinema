package com.sylva.sinema.model

data class Cinema(
    val cinemaId: String,
    val cinemaName: String,
    val cinemaLat: Double,
    val cinemaLng: Double,
    val cinemaStudios: List<Studio>,
    val cinemaMovies: List<Movie>,
    val cinemaSchedules: List<CinemaSchedule>
)

data class CinemaSchedule(
    val scheduleId: String,
    val movie: Movie,
    val studio: Studio,
    val scheduleTime: String
)