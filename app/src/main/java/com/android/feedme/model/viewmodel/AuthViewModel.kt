package com.android.feedme.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel for handling authentication processes, specifically Google authentication in this
 * context.
 */
class AuthViewModel : ViewModel() {

  /**
   * Authenticates a user with Google using an ID token.
   *
   * @param idToken The Google ID Token used to authenticate the user with Firebase Authentication.
   * @param onSuccess Callback to be invoked when authentication is successful.
   * @param onFailure Callback to be invoked when authentication fails with an exception.
   */
  fun authenticateWithGoogle(
      idToken: String,
      onSuccess: () -> Unit,
      onDoesntExist: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Create a Google sign-in credential using the provided Google ID token.
    val credential = GoogleAuthProvider.getCredential(idToken, null)

    // Start a coroutine in the ViewModel's scope.
    viewModelScope.launch {
      try {
        // Attempt to sign in with the Google credential and wait for the result.
        val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        // Process the sign-in result.
        authResult.user?.let { firebaseUser ->
          val googleId = firebaseUser.uid
          val name = firebaseUser.displayName.orEmpty()
          val email = firebaseUser.email.orEmpty()
          val photoUrl = firebaseUser.photoUrl.toString()

          // Try to link or create a profile based on the Firebase User details.
          linkOrCreateProfile(googleId, name, email, photoUrl, onSuccess, onDoesntExist, onFailure)
        } ?: onFailure(Exception("Firebase User is null")) // Handle null user case.
      } catch (e: Exception) {
        // Handle exceptions during the sign-in process.
        onFailure(e)
      }
    }
  }

  /**
   * Links to an existing profile or creates a new one based on Google user data.
   *
   * @param googleId The unique identifier for the Google user.
   * @param name The display name of the Google user.
   * @param email The email address of the Google user.
   * @param photoUrl The URL of the Google user's profile photo.
   * @param onSuccess Callback to be invoked when linking or creation is successful.
   * @param onFailure Callback to be invoked when linking or creation fails with an exception.
   */
  fun linkOrCreateProfile(
      googleId: String,
      name: String?,
      email: String?,
      photoUrl: String?,
      onSuccess: () -> Unit,
      onDoesntExist: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      ProfileRepository.instance.getProfile(
          googleId,
          onSuccess = { existingProfile ->
            if (existingProfile != null) {
              onSuccess()
            } else {
              onDoesntExist()
            }
          },
          onFailure = onFailure)
    }
  }

  /**
   * Creates a new profile and adds it to Firestore.
   *
   * @param googleId The unique identifier for the Google user.
   * @param name The display name of the user (nullable).
   * @param email The email address of the user (nullable).
   * @param photoUrl The URL of the user's profile photo (nullable).
   * @param onSuccess Callback to be invoked when the profile is successfully added.
   * @param onFailure Callback to be invoked when adding the profile fails with an exception.
   */
  fun makeNewProfile(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    FirebaseAuth.getInstance().currentUser?.let { firebaseUser ->
      val googleId = firebaseUser.uid
      val name = firebaseUser.displayName.orEmpty()
      val email = firebaseUser.email.orEmpty()
      val photoUrl = firebaseUser.photoUrl.toString()

      val newProfile =
          Profile(
              id = googleId,
              name = name ?: "",
              username =
                  if (name.length <= 15) name.replace("[^\\w]".toRegex(), "_").lowercase()
                  else name.replace("[^\\w]".toRegex(), "_").take(15).lowercase(),
              email = email ?: "",
              description = "",
              imageUrl = photoUrl ?: "",
              followers = listOf(),
              following = listOf(),
              filter = listOf(),
              recipeList = listOf(),
              commentList = listOf())

      // Add the newly created profile to the Firestore database.
      ProfileRepository.instance.addProfile(newProfile, onSuccess, onFailure)
      onSuccess()
    }
  }

  fun fetchUserProfileOffline(navigationActions: NavigationActions) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser != null) {
      val db = FirebaseFirestore.getInstance()
      db.collection("profiles")
          .document(currentUser.uid)
          .get(Source.CACHE)
          .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
              val userProfile = document.toObject(Profile::class.java)
              if (userProfile != null) {
                // Use the user profile
                navigationActions.navigateTo(TOP_LEVEL_DESTINATIONS[1])
              }
            }
          }
          .addOnFailureListener { e ->
            Log.d("LoginScreen", "Offline sign in failed")
            Log.e("LoginScreen", "Error fetching user profile from cache", e)
          }
    } else {
      Log.e("LoginScreen", "No user is signed in")
    }
  }
}
