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

  // Current User
  var currentUserId: String? = null
  private val _currentUserProfile = MutableStateFlow<Profile?>(null)
  val currentUserProfile: StateFlow<Profile?> = _currentUserProfile
  private val _currentUserFollowers = MutableStateFlow<List<Profile>>(listOf(Profile()))
  private val _currentUserFollowing = MutableStateFlow<List<Profile>>(listOf(Profile()))
  val currentUserFollowers: StateFlow<List<Profile>> = _currentUserFollowers
  val currentUserFollowing: StateFlow<List<Profile>> = _currentUserFollowing

  // Viewing User
  var viewingUserId: String? = null
  private val _viewingUserProfile = MutableStateFlow<Profile?>(null)
  private val _viewingUserFollowing = MutableStateFlow<List<Profile>>(listOf(Profile()))
  private val _viewingUserFollowers = MutableStateFlow<List<Profile>>(listOf(Profile()))
  val viewingUserProfile: StateFlow<Profile?> = _viewingUserProfile
  val viewingUserFollowing: StateFlow<List<Profile>> = _viewingUserFollowing
  val viewingUserFollowers: StateFlow<List<Profile>> = _viewingUserFollowers

  init {
    // Listen to FirebaseAuth state changes
    FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
      val user = firebaseAuth.currentUser
      user?.uid?.let {
        currentUserId = it
        fetchCurrentUserProfile()
      }
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
            _viewingUserProfile.value = profile
            viewingUserId = id
            if (profile != null) {
              fetchProfiles(profile.followers, _viewingUserFollowers)
              fetchProfiles(profile.following, _viewingUserFollowing)
            }
          },
          onFailure = {
            // Handle failure
            throw error("Profile was not fetched during Login")
          })
    }
  }

  fun fetchCurrentUserProfile() {
    viewModelScope.launch {
      repository.getProfile(
          currentUserId!!,
          onSuccess = { profile ->
            _currentUserProfile.value = profile
            if (profile != null) {
              fetchProfiles(profile.followers, _currentUserFollowers)
              fetchProfiles(profile.following, _currentUserFollowing)
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
          onSuccess = { _currentUserProfile.value = profile },
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
   * Returns the profile to show based on if there is viewingUserId is null or not. If null it will
   * show currentUser (the one logged in)
   *
   * @return The profile to show.
   * @throws Exception If no profile is available.
   */
  fun profileToShow(): Profile {
    return (if (isViewingProfile()) viewingUserProfile.value else currentUserProfile.value)
        ?: throw Exception(
            "No Profile to fetch, the current user ID is : $currentUserId, The issue comes from the fact that Firebase has no Profile with that ID")
  }

  /**
   * Checks if the current user is viewing another user's profile. Will only be true if
   * viewingUserId is not null
   *
   * @return True if the current user is viewing another user's profile, false otherwise.
   * @throws Exception If no current user ID is available. Should never happen.
   */
  fun isViewingProfile(): Boolean {
    var isViewingProfile = false
    if (viewingUserId != null && currentUserId != null && currentUserId != viewingUserId) {
      isViewingProfile = true
    } else if (currentUserId == null) {
      // Should never occur
      throw Exception(
          "Not Signed-in : No Current FirebaseUser is sign-in. Database isn't accessible if no one is signed-in")
    }
    return isViewingProfile
  }

  /** Removes the viewing profile. */
  fun removeViewingProfile() {
    viewingUserId = null
  }

  /**
   * Sets the viewing profile.
   *
   * @param profile The profile to set as the viewing profile.
   */
  fun setViewingProfile(profile: Profile) {
    _viewingUserProfile.value = profile
    viewingUserId = profile.id
    fetchProfiles(profile.followers, _viewingUserFollowers)
    fetchProfiles(profile.following, _currentUserFollowing)
  }
}
