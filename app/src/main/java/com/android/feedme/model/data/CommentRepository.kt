package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

class CommentRepository(firestore: FirebaseFirestore) {
  private val db = firestore

  companion object {
    // Placeholder for the singleton instance
    lateinit var instance: CommentRepository
      private set

    // Initialization method to be called once, e.g., in your Application class
    fun initialize(db: FirebaseFirestore) {
      instance = CommentRepository(db)
    }
  }

  fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("comments")
        .document(comment.authorId)
        .set(comment)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun getComment(commentId: String, onSuccess: (Comment?) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("comments")
        .document(commentId)
        .get()
        .addOnSuccessListener { documentSnapshot ->
          val comment = documentSnapshot.toObject(Comment::class.java)
          onSuccess(comment)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("profiles")
        .document(profile.username)
        .set(profile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  fun getProfile(username: String, onSuccess: (Profile?) -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("profiles")
        .document(username)
        .get()
        .addOnSuccessListener { documentSnapshot ->
          val profile = documentSnapshot.toObject(Profile::class.java)
          onSuccess(profile)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }
}
