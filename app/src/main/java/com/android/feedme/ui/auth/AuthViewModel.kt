package com.android.feedme.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel() : ViewModel() {

  // Public function to handle Google sign-in using an ID token.
  fun authenticateWithGoogle(
      idToken: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Get Google credentials from the provided ID token.
    val credential = GoogleAuthProvider.getCredential(idToken, null)

    // Launching a new coroutine in the ViewModel's scope
    viewModelScope.launch {
      try {
        // Asynchronously sign in with the Google credentials and await the result.
        val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        // Assuming successful authentication, proceed to link or create a profile
        authResult.user?.let { firebaseUser ->
          // If sign-in was successful, get user details from FirebaseUser
          val googleId = firebaseUser.uid
          val name = firebaseUser.displayName.orEmpty()
          val email = firebaseUser.email.orEmpty()
          val photoUrl = firebaseUser.photoUrl.toString()

          // Attempt to link existing profile or create a new one.
          linkOrCreateProfile(googleId, name, email, photoUrl, onSuccess, onFailure)
        } ?: onFailure(Exception("Firebase User is null"))
      } catch (e: Exception) {
        // Handle any exceptions that occurred during the sign-in process.
        onFailure(e)
      }
    }
  }

  // Private function to either link to an existing profile or create a new one.
  fun linkOrCreateProfile(
      googleId: String,
      name: String?,
      email: String?,
      photoUrl: String?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Launching a new coroutine for database interaction.
    viewModelScope.launch {
      ProfileRepository.instance.getProfile(
          googleId,
          onSuccess = { existingProfile ->
            // Check if a profile already exists.
            if (existingProfile != null) {
              // If profile exists, call success callback.
              onSuccess()
            } else {
              // If no profile exists, create a new one.
              makeNewProfile(googleId, name, email, photoUrl, onSuccess, onFailure)
            }
          },
          onFailure = onFailure)
    }
  }

  // Private helper function to create a new profile in the Firestore database.
  private fun makeNewProfile(
      googleId: String,
      name: String?,
      email: String?,
      photoUrl: String?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // Create a new Profile data object with default and provided values.
    val newProfile =
        Profile(
            id = googleId,
            name = name ?: "",
            username = name ?: "",
            email = email ?: "",
            description = "",
            imageUrl = photoUrl ?: "",
            followers = listOf(),
            following = listOf(),
            filter = listOf(),
            recipeList = listOf(),
            commentList = listOf())

    // Add the new profile to the Firestore database.
    ProfileRepository.instance.addProfile(newProfile, onSuccess, onFailure)
  }
}
