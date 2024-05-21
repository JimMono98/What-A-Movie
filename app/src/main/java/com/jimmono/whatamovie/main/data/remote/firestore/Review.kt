package com.jimmono.whatamovie.main.data.remote.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

data class Review(
    var id: String= "",
    val userId: String = "",
    val userEmail: String = "",
    val movieTitle: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
fun editReview(review: Review, editedComment: String, rating: Float, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val reviewRef = db.collection("reviews").document(review.id)

    reviewRef.update(
        mapOf(
            "comment" to editedComment,
            "rating" to rating
        )
    )
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}