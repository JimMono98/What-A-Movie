package com.jimmono.whatamovie.main.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

data class User(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val profilePictureUrl: String? = null
)

fun saveUserInfo(user: User) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(user.userId)
        .set(user)
        .addOnSuccessListener {
            Timber.tag("Firestore").d("User information saved successfully!")
        }
        .addOnFailureListener { e ->
            Timber.tag("Firestore").w(e, "Error saving user information")
        }
}

fun getUserInfo(userId: String, onResult: (User?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users").document(userId)
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val user = document.toObject(User::class.java)
                onResult(user)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener { e ->
            Timber.tag("Firestore").w(e, "Error getting user information")
            onResult(null)
        }
}