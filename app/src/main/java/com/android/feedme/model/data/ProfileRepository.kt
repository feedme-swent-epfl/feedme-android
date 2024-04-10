package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

class ProfileRepository(private val db: FirebaseFirestore) {

  private val collectionPath = "profiles"

  companion object {
    // Placeholder for the singleton instance
    lateinit var instance: ProfileRepository
      private set

    // Initialization method to be called once, e.g., in your Application class
    fun initialize(db: FirebaseFirestore) {
      instance = ProfileRepository(db)
    }
  }

  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(profile.id)
        .set(profile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun getProfile(id: String, onSuccess: (Profile?) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(id)
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
