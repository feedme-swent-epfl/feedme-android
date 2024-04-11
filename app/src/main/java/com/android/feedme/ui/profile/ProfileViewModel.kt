package com.android.feedme.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
  private val repository = ProfileRepository.instance
  private val _profile = MutableStateFlow<Profile?>(null)
  val profile: StateFlow<Profile?> = _profile
  val googleId = FirebaseAuth.getInstance().uid

  init {
    if (googleId != null) fetchProfile(googleId)
  }

  fun fetchProfile(id: String) {
    viewModelScope.launch {
      repository.getProfile(
          id,
          onSuccess = { profile -> _profile.value = profile },
          onFailure = {
            // Handle failure, e.g., by setting _profile.value to null or showing an error message
          })
    }
  }
}
