package com.android.feedme.model.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Date

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

    fun mapToComment(
        map: Map<String, Any>,
        onSuccess: (Comment?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Construct the Comment object
            val comment =
                Comment(
                    commentId = map["commentId"] as? String ?: "",
                    content = map["content"] as? String ?: "",
                    creationDate = map["creationDate"] as? Date ?: Date(),
                    photoURL = map["photoUrl"] as? String ?: "",
                    title = map["title"] as? String ?: "",
                    recipeId = map["recipeId"] as? String ?: "",
                    rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
                    userId = map["userid"] as? String ?: "")
            Log.d("RecipeRepository", " Recipe fetched success $comment ")

            onSuccess(comment)
        } catch (e: Exception) {
            onFailure(e)
        }
    }

  /**
   * Adds a comment to Firestore. Will OVERRIDE the comment Id and get a new from firestore.
   *
   * @param comment The Comment object to be added.
   * @param onSuccess Callback invoked on successful addition of the comment.
   * @param onFailure Callback invoked on failure to add the comment, with an exception.
   */
  fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val newDocRef = db.collection("comments").document()
    comment.commentId = newDocRef.id // Assign the generated ID to the comment
    newDocRef
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


    /**
     * A helper function that adds the comments to the list of comments
     *
     * @param snapshot: the snapshot of the query
     * @param onSuccess: the callback function to be called on success
     * @param onFailure: the callback function to be called on failure
     */
    private fun addSuccessListener(
        snapshot: QuerySnapshot,
        onSuccess: (List<Comment>, DocumentSnapshot?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val comments = mutableListOf<Comment>()
        val docs = snapshot.documents

        docs.forEach { commentMap ->
            // Extract the data from the document
            val data = commentMap.data
            if (data != null) {
                // Convert the data to a Recipe object
                mapToComment(
                    data,
                    { comment ->
                        if (comment != null) {
                            comments.add(comment)
                        }
                    },
                    onFailure)
            }
        }
        // Call the success callback with the list of recipes
        onSuccess(comments, docs.lastOrNull())
    }

    fun getComments(
        ids: List<String>,
        onSuccess: (List<Comment>, DocumentSnapshot?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
       // Fetch the comments with the given IDs

        db.collection(collectionPath)
            .whereIn("recipeId", ids)
            .get()
            .addOnSuccessListener { addSuccessListener(it, onSuccess, onFailure) }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
