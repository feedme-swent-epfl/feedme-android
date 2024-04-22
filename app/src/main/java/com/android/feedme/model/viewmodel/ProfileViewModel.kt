package com.android.feedme.model.viewmodel

import android.util.Log
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
  val followers = MutableStateFlow<List<Profile>>(listOf(Profile()))
  val following = MutableStateFlow<List<Profile>>(listOf(Profile()))
  val googleId = FirebaseAuth.getInstance().uid

  init {
    // Listen to FirebaseAuth state changes
    FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      user?.uid?.let { fetchProfile(it) }
    }
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
          onSuccess = { profile ->
            _profile.value = profile
            if (profile != null) {
              fetchProfiles(profile.followers, followers)
              fetchProfiles(profile.following, following)
            }
          },
          onFailure = {
            // Handle failure
            throw error("Profile was not fetched during Login")
          })
    }
  }

  /**
   * A function that set local profile in the database
   *
   * @param profile: the profile to set in the database
   */
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

  /**
   * A function that fetches the profiles of the given Ids
   *
   * @param ids: the unique IDs of the profiles we want to fetch
   * @param fetchProfile: the MutableStateFlow that will store the fetched profiles
   */
  private fun fetchProfiles(ids: List<String>, fetchProfile: MutableStateFlow<List<Profile>>) {
    // Check if we actually need to fetch the profiles
    val currentIds = fetchProfile.value.map { it.id }.toSet()
    if (currentIds != ids.toSet() && ids.isNotEmpty()) {
      Log.d("ProfileViewModel", "Fetching profiles: $ids")
      viewModelScope.launch {
        repository.getProfiles(
            ids,
            onSuccess = { profiles ->
              // Avoid unnecessary updates
              if (fetchProfile.value != profiles) {
                fetchProfile.value = profiles
              } else {
                Log.d("ProfileViewModel", "Profiles already fetched")
              }
            },
            onFailure = {
              // Handle failure
              throw error("Profiles were not fetched")
            })
      }
    }
  }

  /**
   * Retrieves the current profile from the [profile] StateFlow.
   *
   * @return The current profile if available, or null if no profile is available.
   */
  fun getProfile(): Profile? {
    return profile.value
  }
}
