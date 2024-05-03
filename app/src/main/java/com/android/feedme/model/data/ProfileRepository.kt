package com.android.feedme.model.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.FirebaseStorage

/**
 * A repository class for managing user profiles in Firebase Firestore.
 *
 * Provides functionalities to add and retrieve user profiles. Utilizes a singleton pattern for
 * using a single instance throughout the application to interact with the Firestore database.
 *
 * @property db The Firestore database instance used for profile operations.
 */
class ProfileRepository(private val db: FirebaseFirestore) {

  companion object {
    /** The singleton instance of the ProfileRepository. */
    lateinit var instance: ProfileRepository
      private set

    /**
     * Initializes the singleton instance of ProfileRepository with a Firestore database instance.
     * This method should be called once, typically during the application's initialization phase.
     *
     * @param db The FirebaseFirestore instance for database operations.
     */
    fun initialize(db: FirebaseFirestore) {
      instance = ProfileRepository(db)
    }
  }

  private val collectionPath = "profiles"

  /**
   * Adds a user profile to Firestore.
   *
   * This method saves the profile document in the Firestore collection specified by
   * [collectionPath]. On successful addition, [onSuccess] is called; if an error occurs,
   * [onFailure] is invoked with the exception.
   *
   * @param profile The Profile object to be added to Firestore.
   * @param onSuccess A callback function invoked on successful addition of the profile.
   * @param onFailure A callback function invoked on failure to add the profile, with an exception.
   */
  fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(collectionPath)
        .document(profile.id)
        .set(profile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * Uploads a user profile picture in Firestore.
   *
   * This method updates the profile document in the Firestore collection specified by
   * [collectionPath]. If an error occurs, [onFailure] is invoked with the exception.
   *
   * @param profile The Profile object to be updated in Firestore.
   * @param uri The picture to upload.
   * @param onFailure A callback function invoked on failure to update the profile, with an
   *   exception.
   */
  fun uploadProfilePicture(profile: Profile?, uri: Uri, onFailure: (Exception) -> Unit) {
    if (profile != null) {
      val storageRef =
          FirebaseStorage.getInstance().reference.child("profilePictures/${profile.id}")
      storageRef
          .putFile(uri)
          .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
              val url = uri.toString()
              db.collection("profiles")
                  .document(profile.id)
                  .update("imageUrl", url)
                  .addOnFailureListener { exception -> onFailure(exception) }
            }
          }
          .addOnFailureListener { exception -> onFailure(exception) }
    }
  }
  /**
   * Retrieves a user profile from Firestore by its document ID.
   *
   * Fetches the profile document with the given [id] from the Firestore collection specified by
   * [collectionPath]. If successful, [onSuccess] is called with the retrieved Profile object; if an
   * error occurs or the document does not exist, [onFailure] is invoked.
   *
   * @param id The document ID of the profile to retrieve.
   * @param onSuccess A callback function invoked with the retrieved Profile object on success.
   * @param onFailure A callback function invoked on failure to retrieve the profile, with an
   *   exception.
   */
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

  /**
   * Fetch all the profiles of the given List of Ids
   *
   * @param ids The list of profile IDs to fetch.
   * @param onSuccess A callback function invoked with the list of profiles on success.
   * @param onFailure A callback function invoked on failure to fetch the profiles, with an
   *   exception.
   */
  fun getProfiles(
      ids: List<String>,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(collectionPath)
        .whereIn("id", ids)
        .get()
        .addOnSuccessListener { querySnapshot ->
          val profiles = querySnapshot.toObjects(Profile::class.java)
          onSuccess(profiles)
        }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * follow a user, adding the target user from the current user's following list and the current
   * user from the target user's followers list. This method is transactional, ensuring that both
   * operations succeed or fail together.
   *
   * @param currentUserId The ID of the user who is following.
   * @param toFollowId The ID of the user to follow.
   * @param onSuccess A callback function invoked on successful following.
   * @param onFailure A callback function invoked on failure to follow, with an exception.
   */
  fun followUser(
      currentUserId: String,
      toFollowId: String,
      onSuccess: (Profile, Profile) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    handleFirestoreTransaction(
        {
          val currentUserFRef = db.collection(collectionPath).document(currentUserId)
          val targetUserFRef = db.collection(collectionPath).document(toFollowId)
          val currentUser =
              this.get(currentUserFRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val toFollowUser =
              this.get(targetUserFRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null

          // Update current user's following list
          val currentFollowing = currentUser.following.toMutableList()
          if (!currentFollowing.contains(toFollowId)) {
            currentFollowing.add(toFollowId)
            currentUser.following = currentFollowing // Update the local object
            update(currentUserFRef, "following", currentFollowing)
          }

          // Update target user's followers list
          val targetFollowers = toFollowUser.followers.toMutableList()
          if (!targetFollowers.contains(currentUserId)) {
            targetFollowers.add(currentUserId)

            toFollowUser.followers = targetFollowers // Update the local object
            update(targetUserFRef, "followers", targetFollowers)
          }

          set(currentUserFRef, currentUser)

          set(targetUserFRef, toFollowUser)

          Pair(currentUser, toFollowUser) // Returning the Pair of updated profiles
        },
        {
          it as Pair<Profile, Profile> // Cast result to Pair<Profile, Profile>
          onSuccess(it.first, it.second) // Call onSuccess with the updated profiles
        },
        onFailure)
  }

  /**
   * Unfollows a user, removing the target user from the current user's following list and the
   * current user from the target user's followers list. This method is transactional, ensuring that
   * both operations succeed or fail together.
   *
   * @param currentUserId The ID of the user who is unfollowing.
   * @param targetUserId The ID of the user to unfollow.
   * @param onSuccess A callback function invoked on successful unfollowing.
   * @param onFailure A callback function invoked on failure to unfollow, with an exception.
   */
  fun unfollowUser(
      currentUserId: String,
      targetUserId: String,
      onSuccess: (Profile, Profile) -> Unit, // Updated to pass Profile objects
      onFailure: (Exception) -> Unit
  ) {
    handleFirestoreTransaction(
        {
          val currentUserRef = db.collection(collectionPath).document(currentUserId)
          val targetUserRef = db.collection(collectionPath).document(targetUserId)
          val currentUser =
              this.get(currentUserRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val targetUser =
              this.get(targetUserRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null

          // Prepare the updated lists
          val currentFollowing = currentUser.following.toMutableList()
          if (currentFollowing.contains(targetUserId)) {
            currentFollowing.remove(targetUserId)
            currentUser.following = currentFollowing // Update the local object
          }

          val targetFollowers = targetUser.followers.toMutableList()
          if (targetFollowers.contains(currentUserId)) {
            targetFollowers.remove(currentUserId)
            targetUser.followers = targetFollowers // Update the local object
          }

          // Perform the updates in the database
          update(currentUserRef, "following", currentFollowing)
          update(targetUserRef, "followers", targetFollowers)

          // Return the updated profiles to be used in the onSuccess callback
          set(currentUserRef, currentUser)
          set(targetUserRef, targetUser)

          Pair(currentUser, targetUser) // Returning the Pair of updated profiles
        },
        {
          it as Pair<Profile, Profile> // Cast result to Pair<Profile, Profile>
          onSuccess(it.first, it.second) // Call onSuccess with the updated profiles
        },
        onFailure)
  }

  private fun <T> handleFirestoreTransaction(
      transactionBlock: Transaction.() -> T,
      onSuccess: (T) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.runTransaction { transaction ->
          transactionBlock(transaction) // Execute the specific transaction logic
        }
        .addOnSuccessListener { result ->
          onSuccess(result) // Handle success with the result of the transaction
        }
        .addOnFailureListener { exception ->
          onFailure(exception) // Handle failure
        }
  }
}
