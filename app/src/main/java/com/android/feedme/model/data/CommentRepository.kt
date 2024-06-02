package com.android.feedme.model.data

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * A repository class for managing comments in Firebase Firestore.
 *
 * This class provides methods to add and retrieve comments from Firestore. It follows the singleton
 * pattern to ensure a single instance of the repository is used throughout the application.
 *
 * @property db The Firestore database instance used for comment operations.
 */
class CommentRepository(private val db: FirebaseFirestore) {

  val collectionPath = "comments"
  val databasePath = "comments/"

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
   * Adds a comment to Firestore. Will OVERRIDE the comment Id and get a new from firestore.
   *
   * @param comment The Comment object to be added.
   * @param onSuccess Callback invoked on successful addition of the comment.
   * @param onFailure Callback invoked on failure to add the comment, with an exception.
   */
  fun addComment(
      comment: Comment,
      uri: Uri?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val newDocRef = db.collection(collectionPath).document()
    comment.commentId = newDocRef.id // Assign the generated ID to the comment
    if (uri != null) {
      val storageRef =
          FirebaseStorage.getInstance().reference.child((databasePath + comment.commentId))
      storageRef
          .putFile(uri)
          .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
              comment.photoURL = uri.toString()
              setInDatabase(comment, newDocRef, onSuccess, onFailure)
            }
          }
          .addOnFailureListener { exception -> onFailure(exception) }
    } else {
      comment.photoURL =
          "https://firebasestorage.googleapis.com/v0/b/feedme-33341.appspot.com/o/recipestest%2Fdummy.jpg?alt=media&token=71de581c-9e1e-47c8-a4dc-8cccf1d0b640"
      setInDatabase(comment, newDocRef, onSuccess, onFailure)
    }
  }

  private fun setInDatabase(
      comment: Comment,
      id: DocumentReference,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    id.set(comment).addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
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
