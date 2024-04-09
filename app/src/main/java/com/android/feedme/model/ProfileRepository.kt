package com.android.feedme.model

import com.android.feedme.model.data.Profile
import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository(private val db: FirebaseFirestore) {

  private val collectionPath = "profiles"

  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(profile.username)
        .set(profile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun getProfile(username: String, onSuccess: (Profile?) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(username)
        .get()
        .addOnSuccessListener { documentSnapshot ->
          val profile = documentSnapshot.toObject(Profile::class.java)
          onSuccess(profile)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  // Additional methods for updating and deleting profiles can be implemented following the same
  // pattern
}
