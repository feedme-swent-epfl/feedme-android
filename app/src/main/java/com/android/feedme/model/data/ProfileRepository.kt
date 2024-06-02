package com.android.feedme.model.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.displayToast
import com.android.feedme.model.viewmodel.isNetworkAvailable
import com.google.firebase.firestore.DocumentSnapshot
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
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful addition of the profile.
   * @param onFailure A callback function invoked on failure to add the profile, with an exception.
   */
  fun addProfile(
      profile: Profile,
      context: Context,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("addProfile", "Offline mode: Cannot add profile")
      displayToast(context)
      return
    }

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
   * @param profileViewModel The ViewModel to update the imageUrl field.
   * @param uri The picture to upload.
   * @param context The context to check if the user is offline
   * @param onFailure A callback function invoked on failure to update the profile, with an
   *   exception.
   */
  fun uploadProfilePicture(
      profileViewModel: ProfileViewModel,
      uri: Uri,
      context: Context,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("uploadProfilePicture", "Offline mode: Cannot upload profile picture")
      displayToast(context)
      return
    }

    val storageRef =
        FirebaseStorage.getInstance()
            .reference
            .child("profilePictures/${profileViewModel.currentUserId}")
    storageRef
        .putFile(uri)
        .addOnSuccessListener { taskSnapshot ->
          taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
            val url = uri.toString()
            db.collection("profiles")
                .document(profileViewModel.currentUserId!!)
                .update("imageUrl", url)
                .addOnFailureListener { exception -> onFailure(exception) }
            profileViewModel._imageUrl.value = url
          }
        }
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

  /**
   * Fetch all the profiles of the given List of Ids
   *
   * @param ids The list of profile IDs to fetch.
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked with the list of profiles on success.
   * @param onFailure A callback function invoked on failure to fetch the profiles, with an
   *   exception.
   */
  fun getProfiles(
      ids: List<String>,
      context: Context,
      onSuccess: (List<Profile>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getProfiles", "Offline mode: Cannot fetch profiles")
      displayToast(context)
      return
    }

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
   * Fetch all the profiles that contain the given query in their name.
   *
   * @param query The search query to filter the profiles.
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked with the list of profiles on success.
   * @param onFailure A callback function invoked on failure to fetch the profiles, with an
   *   exception.
   */
  fun getFilteredProfiles(
      query: String,
      context: Context,
      lastProfile: DocumentSnapshot?,
      onSuccess: (List<Profile>, DocumentSnapshot?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("getFilteredProfiles", "Offline mode: Cannot fetch profiles")
      return
    }

    var queryRef =
        db.collection(collectionPath)
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThan("username", query + "\uf8ff")
            .limit(10)

    if (lastProfile != null) {
      queryRef = queryRef.startAfter(lastProfile)
    }

    queryRef
        .get()
        .addOnSuccessListener {
          val profiles = mutableListOf<Profile>()
          val docs = it.documents

          docs.forEach { recipeMap ->
            // Extract the data from the document
            val data = recipeMap.data
            if (data != null) {
              // Convert the data to a Profile object
              mapToProfile(
                  data,
                  { profile ->
                    if (profile != null) {
                      profiles.add(profile)
                    }
                  },
                  onFailure)
            }
          }
          // Call the success callback with the list of profiles
          onSuccess(profiles, docs.lastOrNull())
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Deletes a user profile from Firestore.
   *
   * This method removes the profile document with the given [id] from the Firestore collection
   * specified by [collectionPath]. On successful deletion, [onSuccess] is called; if an error
   * occurs, [onFailure] is invoked with the exception.
   *
   * @param id The document ID of the profile to delete.
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful deletion of the profile.
   * @param onFailure A callback function invoked on failure to delete the profile, with an
   *   exception.
   */
  fun deleteProfile(
      id: String,
      context: Context,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("deleteProfile", "Offline mode: Cannot delete profile")
      displayToast(context)
      return
    }

    db.collection(collectionPath)
        .document(id)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
  }

  /**
   * follow a user, adding the target user from the current user's following list and the current
   * user from the target user's followers list. This method is transactional, ensuring that both
   * operations succeed or fail together.
   *
   * @param currentUserId The ID of the user who is following.
   * @param toFollowId The ID of the user to follow.
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful following.
   * @param onFailure A callback function invoked on failure to follow, with an exception.
   */
  fun followUser(
      currentUserId: String,
      toFollowId: String,
      context: Context,
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
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful unfollowing.
   * @param onFailure A callback function invoked on failure to unfollow, with an exception.
   */
  fun unfollowUser(
      currentUserId: String,
      targetUserId: String,
      context: Context,
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
    // }
  }

  private fun mapToProfile(
      map: Map<String, Any>,
      onSuccess: (Profile?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val rawFollowersList = map["followers"]
      val followers =
          if (rawFollowersList is List<*>) {
            rawFollowersList.mapNotNull { it as? String }
          } else {
            listOf()
          }

      val rawFollowingList = map["following"]
      val following =
          if (rawFollowingList is List<*>) {
            rawFollowingList.mapNotNull { it as? String }
          } else {
            listOf()
          }

      val rawFilterList = map["filter"]
      val filter =
          if (rawFilterList is List<*>) {
            rawFilterList.mapNotNull { it as? String }
          } else {
            listOf()
          }

      val rawRecipeList = map["recipeList"]
      val recipeList =
          if (rawRecipeList is List<*>) {
            rawRecipeList.mapNotNull { it as? String }
          } else {
            listOf()
          }

      val rawCommentList = map["commentList"]
      val commentList =
          if (rawCommentList is List<*>) {
            rawCommentList.mapNotNull { it as? String }
          } else {
            listOf()
          }

      // Construct the Profile object
      val profile =
          Profile(
              id = map["id"] as? String ?: "ID_DEFAULT",
              name = map["name"] as? String ?: "NAME_DEFAULT",
              username = map["username"] as? String ?: "USERNAME_DEFAULT",
              email = map["email"] as? String ?: "EMAIL_DEFAULT",
              description = map["description"] as? String ?: "BIO_DEFAULT",
              imageUrl = map["imageUrl"] as? String ?: "URL_DEFAULT",
              followers = followers,
              following = following,
              filter = filter,
              recipeList = recipeList,
              commentList = commentList)

      onSuccess(profile)
    } catch (e: Exception) {
      onFailure(e)
    }
  }

  /**
   * Function that updates a document in Firestore with the given field and value.
   *
   * @param userId The ID of the user to update the saved recipe for
   * @param recipe The [Recipe] to add to the user's saved recipes
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful addition of the recipe
   * @param onFailure A callback function invoked on failure to add the recipe, with an exception
   */
  fun addSavedRecipe(
      userId: String,
      recipe: String,
      context: Context,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (!isNetworkAvailable(context)) {
      Log.d("addSavedRecipe", "Offline mode: Cannot add saved recipe")
      displayToast(context)
      return
    }

    handleFirestoreTransaction(
        {
          val userRef = db.collection(collectionPath).document(userId)
          val currentUser =
              this.get(userRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val savedRecipes = currentUser.savedRecipes.toMutableList()
          savedRecipes.add(recipe)
          currentUser.savedRecipes = savedRecipes
          update(userRef, "savedRecipes", savedRecipes)
          set(userRef, currentUser)
        },
        onSuccess = { onSuccess() },
        onFailure = { onFailure(it) })
  }

  /**
   * Function that updates a document in Firestore with the given field and value.
   *
   * @param userId The ID of the user to update the saved recipe for
   * @param recipe The [Recipe] to add to the user's saved recipes
   * @param onSuccess A callback function invoked on successful addition of the recipe
   * @param onFailure A callback function invoked on failure to add the recipe, with an exception
   */
  fun linkRecipeToProfile(
      userId: String,
      recipe: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    handleFirestoreTransaction(
        {
          val userRef = db.collection(collectionPath).document(userId)
          val currentUser =
              this.get(userRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val savedRecipes = currentUser.recipeList.toMutableList()
          savedRecipes.add(recipe)
          currentUser.recipeList = savedRecipes
          update(userRef, "recipeList", savedRecipes)
          set(userRef, currentUser)
        },
        onSuccess = { onSuccess() },
        onFailure = { onFailure(it) })
  }

  /**
   * Function that updates a document in Firestore with the given field and value.
   *
   * @param userId The ID of the user to update the saved recipe for
   * @param recipe The [Recipe] to add to the user's saved recipes
   * @param onSuccess A callback function invoked on successful addition of the recipe
   * @param onFailure A callback function invoked on failure to add the recipe, with an exception
   */
  fun addCommentToProfile(
      userId: String,
      commentId: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    handleFirestoreTransaction(
        {
          val userRef = db.collection(collectionPath).document(userId)
          val currentUser =
              this.get(userRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val savedRecipes = currentUser.commentList.toMutableList()
          savedRecipes.add(commentId)
          currentUser.commentList = savedRecipes
          update(userRef, "commentList", savedRecipes)
          set(userRef, currentUser)
        },
        onSuccess = { onSuccess() },
        onFailure = { onFailure(it) })
  }

  /**
   * Function that removes a saved recipe from a user's saved recipes in Firestore.
   *
   * @param userId The ID of the user to remove the saved recipe for
   * @param recipe The [Recipe] to remove from the user's saved recipes
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful removal of the recipe
   * @param onFailure A callback function invoked on failure to remove the recipe, with an exception
   */
  fun removeSavedRecipe(
      userId: String,
      recipe: String,
      context: Context,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (!isNetworkAvailable(context)) {
      Log.d("removeSavedRecipe", "Offline mode: Cannot remove saved recipe")
      displayToast(context)
      return
    }

    handleFirestoreTransaction(
        {
          val userRef = db.collection(collectionPath).document(userId)
          val currentUser =
              this.get(userRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          val savedRecipes = currentUser.savedRecipes.toMutableList().filter { it != recipe }
          currentUser.savedRecipes = savedRecipes
          update(userRef, "savedRecipes", savedRecipes)
          set(userRef, currentUser)
        },
        onSuccess = { onSuccess() },
        onFailure = { onFailure(it) })
  }

  /**
   * Function that checks if a saved recipe exists in a user's saved recipes in Firestore.
   *
   * @param userId The ID of the user to check the saved recipe for
   * @param recipe The [Recipe] to check if it exists in the user's saved recipes
   * @param context The context to check if the user is offline
   * @param onResult A callback function invoked with the result of the check
   * @param onFailure A callback function invoked on failure to check the recipe, with an exception
   */
  fun savedRecipeExists(
      userId: String,
      recipe: String,
      context: Context,
      onResult: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("savedRecipeExists", "Offline mode: Cannot check saved recipe")
      return
    }

    val userRef = db.collection(collectionPath).document(userId)
    userRef
        .get()
        .addOnSuccessListener { snapshot ->
          val savedRecipes = snapshot["savedRecipes"] as? List<String> ?: emptyList()
          onResult(savedRecipes.contains(recipe))
        }
        .addOnFailureListener { onFailure(it) }
  }

  /**
   * Function that modifies the showDialog field in a user's profile in Firestore. This field is
   * used to determine if the user should be shown a dialog on login.
   *
   * @param userId The ID of the user to modify the showDialog field for
   * @param showDialog The value to set the showDialog field to
   * @param context The context to check if the user is offline
   * @param onSuccess A callback function invoked on successful modification of the showDialog field
   * @param onFailure A callback function invoked on failure to modify the showDialog field, with an
   *   exception
   */
  fun modifyShowDialog(
      userId: String,
      showDialog: Boolean,
      context: Context,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Check if the user is offline
    if (!isNetworkAvailable(context)) {
      Log.d("modifyShowDialog", "Offline mode: Cannot modify showDialog")
      displayToast(context)
      return
    }

    handleFirestoreTransaction(
        {
          val userRef = db.collection(collectionPath).document(userId)
          val currentUser =
              this.get(userRef).toObject(Profile::class.java)
                  ?: return@handleFirestoreTransaction null
          currentUser.showDialog = showDialog
          update(userRef, "showDialog", showDialog)
          set(userRef, currentUser)
        },
        onSuccess = { onSuccess() },
        onFailure = { onFailure(it) })
  }
}
