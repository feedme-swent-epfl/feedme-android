package com.android.feedme.model.data

import com.google.firebase.firestore.FirebaseFirestore

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
}
