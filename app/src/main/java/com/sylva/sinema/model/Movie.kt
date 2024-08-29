package com.sylva.sinema.model

import android.os.Parcel
import android.os.Parcelable

data class Movie(
    val movieId: String? = "",
    val movieName: String? = "",
    val movieDuration: Int? = 0,
    val description: String? = "",
    val rating: String? = "",
    val posterUrl: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(movieId)
        parcel.writeString(movieName)
        if (movieDuration != null) {
            parcel.writeInt(movieDuration)
        }
        parcel.writeString(description)
        parcel.writeValue(rating)
        parcel.writeString(posterUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }
}