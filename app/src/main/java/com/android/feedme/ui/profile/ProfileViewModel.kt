package com.android.feedme.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A class that generates the profile view model
 *
 * This class provides the link between the Profile database and the rest of the code. It can be
 * used in order to extract the profile information of the connected user
 */
class ProfileViewModel : ViewModel() {
  private val repository = ProfileRepository.instance
  private val _profile = MutableStateFlow<Profile?>(null)
  val profile: StateFlow<Profile?> = _profile
  val googleId = FirebaseAuth.getInstance().uid

  init {
    if (googleId != null) fetchProfile(googleId)
  }

  /**
   * A function that fetches the profile during Login
   *
   * @param id: the unique ID of the profile we want to fetch
   */
  fun fetchProfile(id: String) {
    viewModelScope.launch {
      repository.getProfile(
          id,
          onSuccess = { profile -> _profile.value = profile },
          onFailure = {
            // Handle failure
            throw error("Profile was not fetched during Login")
          })
    }
  }

  fun setProfile(profile: Profile) {
    viewModelScope.launch {
      repository.addProfile(
          profile,
          onSuccess = { _profile.value = profile },
          onFailure = {
            // Handle failure
            throw error("Profile could not get updated")
          })
    }
  }
}