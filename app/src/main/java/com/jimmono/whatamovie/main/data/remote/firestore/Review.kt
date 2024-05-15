package com.jimmono.whatamovie.main.data.remote.firestore

import com.google.firebase.Timestamp

data class Review(
    val userId: String = "",
    val userName: String = "",
    val movieTitle: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now()
)