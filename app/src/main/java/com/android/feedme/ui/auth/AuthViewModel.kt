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

  fun authenticateWithGoogle(
      idToken: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    viewModelScope.launch {
      try {
        val authResult = FirebaseAuth.getInstance().signInWithCredential(credential).await()
        // Assuming successful authentication, proceed to link or create a profile
        authResult.user?.let { firebaseUser ->
          val googleId = firebaseUser.uid
          val name = firebaseUser.displayName.orEmpty()
          val email = firebaseUser.email.orEmpty()
          val photoUrl = firebaseUser.photoUrl.toString()
          linkOrCreateProfile(googleId, name, email, photoUrl, onSuccess, onFailure)
        } ?: onFailure(Exception("Firebase User is null"))
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  fun linkOrCreateProfile(
      googleId: String,
      name: String?,
      email: String?,
      photoUrl: String?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      ProfileRepository.instance.getProfile(
          googleId,
          onSuccess = { existingProfile ->
            if (existingProfile != null) {
              onSuccess()
            } else {
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
              ProfileRepository.instance.addProfile(newProfile, onSuccess, onFailure)
            }
          },
          onFailure = onFailure)
    }
  }
}
