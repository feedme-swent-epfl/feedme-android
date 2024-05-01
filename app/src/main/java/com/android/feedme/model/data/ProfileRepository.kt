package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.toObject

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
  /** Fetch all the profiles of the given List of Ids */
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
   * Follows another user, updating both the current user's following list and the other user's
   * followers list.
   */
  fun followUser(
      currentUserId: String,
      targetUserId: String,
      onSuccess: (Profile, Profile) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    handleFirestoreTransaction(
        {
          val currentUserRef = db.collection(collectionPath).document(currentUserId)
          val targetUserRef = db.collection(collectionPath).document(targetUserId)
          val currentUser =
              get(currentUserRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val targetUser =
              get(targetUserRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null

          // Update current user's following list
          val currentFollowing = currentUser?.following?.toMutableList() ?: mutableListOf()
          if (!currentFollowing.contains(targetUserId)) {
            currentFollowing.add(targetUserId)
            currentUser.following = currentFollowing // Update the local object
            update(currentUserRef, "following", currentFollowing)
          }

          // Update target user's followers list
          val targetFollowers = targetUser?.followers?.toMutableList() ?: mutableListOf()
          if (!targetFollowers.contains(currentUserId)) {
            targetFollowers.add(currentUserId)

            targetUser.followers = targetFollowers // Update the local object
            update(targetUserRef, "followers", targetFollowers)
          }

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
              get(currentUserRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val targetUser =
              get(targetUserRef).toObject(Profile::class.java)
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

  /**
   * Removes a follower from the specified user's followers list and updates the following list of
   * the follower. This method is transactional, ensuring that both operations succeed or fail
   * together.
   *
   * @param userId The ID of the user who will lose a follower.
   * @param followerId The ID of the follower to remove.
   * @param onSuccess A callback function invoked on successful removal of the follower.
   * @param onFailure A callback function invoked on failure to remove the follower, with an
   *   exception.
   */
  fun removeFollower(
      userId: String,
      followerId: String,
      onSuccess: (Profile, Profile) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    handleFirestoreTransaction(
        {
          // Define Firestore document references for the user and the follower
          val userDocRef = db.collection(collectionPath).document(userId)
          val followerDocRef = db.collection(collectionPath).document(followerId)

          // Attempt to fetch the current state of the user and the follower from Firestore
          val user =
              get(userDocRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val follower =
              get(followerDocRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null

          // Remove the followerId from the user's followers list if it exists
          val userFollowers = user.followers.toMutableList()
          if (userFollowers.contains(followerId)) {
            userFollowers.remove(followerId)
            user.followers = userFollowers
            update(userDocRef, "followers", userFollowers) // Update the user document in Firestore
          }

          // Remove the userId from the follower's following list if it exists
          val followerFollowing = follower.following.toMutableList()
          if (followerFollowing.contains(userId)) {
            followerFollowing.remove(userId)
            follower.following = followerFollowing
            update(
                followerDocRef,
                "following",
                followerFollowing) // Update the follower document in Firestore
          }

          // Return the updated profiles as a Pair to be passed to onSuccess
          Pair(user, follower)
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
