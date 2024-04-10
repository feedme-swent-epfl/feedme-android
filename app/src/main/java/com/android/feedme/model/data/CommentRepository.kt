package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

/**
 * A repository class for managing comments in Firebase Firestore.
 *
 * This class provides methods to add and retrieve comments from Firestore. It follows the singleton
 * pattern to ensure a single instance of the repository is used throughout the application.
 *
 * @property db The Firestore database instance used for comment operations.
 */
class CommentRepository(private val db: FirebaseFirestore) {

  companion object {
    /** The singleton instance of CommentRepository. */
    lateinit var instance: CommentRepository
      private set

    /**
     * Initializes the singleton instance of CommentRepository. This method should be called once,
     * typically during application startup.
     *
     * @param db The FirebaseFirestore instance to be used by the repository.
     */
    fun initialize(db: FirebaseFirestore) {
      instance = CommentRepository(db)
    }
  }

  /**
   * Adds a comment to Firestore.
   *
   * @param comment The Comment object to be added.
   * @param onSuccess Callback invoked on successful addition of the comment.
   * @param onFailure Callback invoked on failure to add the comment, with an exception.
   */
  fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection("comments")
        .document(comment.authorId)
        .set(comment)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Retrieves a comment from Firestore by its ID.
   *
   * @param commentId The ID of the comment to be retrieved.
   * @param onSuccess Callback invoked on successful retrieval of the comment, with the comment
   *   object.
   * @param onFailure Callback invoked on failure to retrieve the comment, with an exception.
   */
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
}
