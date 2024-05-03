package com.android.feedme.model.viewmodel

import android.net.Uri
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
  private val _currentUserFollowers = MutableStateFlow<List<Profile>>(listOf())
  private val _currentUserFollowing = MutableStateFlow<List<Profile>>(listOf())
  val currentUserFollowers: StateFlow<List<Profile>> = _currentUserFollowers
  val currentUserFollowing: StateFlow<List<Profile>> = _currentUserFollowing
  val _imageUrl = MutableStateFlow<String?>(null)

  // Viewing User
  var viewingUserId: String? = null
  private val _viewingUserProfile = MutableStateFlow<Profile?>(null)
  private val _viewingUserFollowing = MutableStateFlow<List<Profile>>(listOf())
  private val _viewingUserFollowers = MutableStateFlow<List<Profile>>(listOf())
  val viewingUserProfile: StateFlow<Profile?> = _viewingUserProfile
  val viewingUserFollowing: StateFlow<List<Profile>> = _viewingUserFollowing
  val viewingUserFollowers: StateFlow<List<Profile>> = _viewingUserFollowers

  // Listen to FirebaseAuth state changes, to fetch the profile of the current user
  private val authListener =
      FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        user?.uid?.let {
          currentUserId = it
          fetchCurrentUserProfile()
        }
      }

  // Error message
  private val _errorMessages = MutableStateFlow<String?>(null)
  val errorMessages: StateFlow<String?> = _errorMessages

  init {
    // Listen to FirebaseAuth state changes
    FirebaseAuth.getInstance().addAuthStateListener(authListener)
    // Listen to FirebaseStorage profile picture changes

  }

  override fun onCleared() {
    FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    super.onCleared()
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
    currentUserId?.let { userId ->
      viewModelScope.launch {
        repository.getProfile(
            userId,
            onSuccess = { profile ->
              _currentUserProfile.value = profile
              if (profile != null) {
                _imageUrl.value = profile.imageUrl
                fetchProfiles(profile.followers, _currentUserFollowers)
                fetchProfiles(profile.following, _currentUserFollowing)
              }
            },
            onFailure = {
              // Handle failure
              _errorMessages.value = "Failed to fetch profile"
              Log.d("ProfileViewModel", "Failed to fetch profile")
            })
      }
    } ?: Log.e("ProfileViewModel", "Current user ID is null.")
  }

  /**
   * A function that set local profile in the database
   *
   * @param profile: the profile to set in the database
   */
  fun setProfile(profile: Profile, isCurrent: Boolean = true) {
    viewModelScope.launch {
      repository.addProfile(
          profile,
          onSuccess = {
            if (isCurrent) {
              updateCurrentUserProfile(profile)
            }
          },
          onFailure = { _errorMessages.value = "Profile could not get updated" })
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
    fetchProfiles(profile.following, _viewingUserFollowing)
  }

  /**
   * Updates the current user's profile.
   *
   * @param profile The updated profile.
   */
  fun updateCurrentUserProfile(profile: Profile) {
    _currentUserProfile.value = profile
    currentUserId = profile.id
    fetchProfiles(profile.followers, _currentUserFollowers)
    fetchProfiles(profile.following, _currentUserFollowing)
  }

  /**
   * Starts following another user, updating both the current user's following list and the other
   * user's followers list. This method is transactional, ensuring that both operations succeed or
   * fail together.
   *
   * @param targetUser The Profile of the user being followed.
   */
  fun followUser(targetUser: Profile) {
    if (currentUserId == null) {
      return
    }
    viewModelScope.launch {
      repository.followUser(
          currentUserId!!,
          targetUser.id,
          onSuccess = { curr, target ->
            Log.d("ProfileViewModel", "Successfully started following the user")
            _currentUserProfile.value = curr
            _currentUserFollowing.value += target
            _currentUserFollowers.value =
                _currentUserFollowers.value.map {
                  if (it.id == target.id) {
                    target
                  } else {
                    it
                  }
                }
            if (viewingUserId == target.id) {
              _viewingUserProfile.value = target

              _viewingUserFollowers.value = _viewingUserFollowers.value.plus(curr)
              _viewingUserFollowing.value =
                  _viewingUserFollowing.value.map { user ->
                    if (user.id == currentUserId) {
                      curr
                    } else {
                      user
                    }
                  }
            }
          },
          onFailure = { error ->
            _errorMessages.value = "Failed to start following user: ${error.message}"
            Log.e("ProfileViewModel", "Failed to start following user: ${error.message}")
          })
    }
  }

  /**
   * Unfollows another user, updating both the current user's following list and the other user's
   * followers list. This method is transactional, ensuring that both operations succeed or fail
   * together.
   *
   * @param targetUser The Profile of the user being unfollowed.
   */
  fun unfollowUser(targetUser: Profile) {
    if (currentUserId == null) {
      return
    }
    viewModelScope.launch {
      repository.unfollowUser(
          currentUserId!!,
          targetUser.id,
          onSuccess = { curr, target ->
            Log.d("ProfileViewModel", "Successfully unfollowed the user")
            _currentUserProfile.value = curr
            _currentUserFollowing.value = _currentUserFollowing.value.filter { it.id != target.id }
            _currentUserFollowers.value =
                _currentUserFollowers.value.map {
                  if (it.id == target.id) {
                    target
                  } else {
                    it
                  }
                }
            if (viewingUserId == target.id) {
              _viewingUserProfile.value = target
              _viewingUserFollowers.value =
                  _viewingUserFollowers.value.filter { it.id != currentUserId }
              _viewingUserFollowing.value =
                  _viewingUserFollowing.value.map {
                    if (it.id == currentUserId) {
                      curr
                    } else {
                      it
                    }
                  }
            }
          },
          onFailure = { error ->
            _errorMessages.value = "Failed to unfollow user: ${error.message}"
            Log.e("ProfileViewModel", "Failed to unfollow user: ${error.message}")
          })
    }
  }

  /**
   * Removes a follower from the user's followers list and the user from the follower's following
   * list. This method is transactional, ensuring that both operations succeed or fail together.
   *
   * @param follower The Profile of the follower to remove.
   */
  fun removeFollower(follower: Profile) {
    if (currentUserId == null) {
      return
    }
    viewModelScope.launch {
      repository.unfollowUser(
          follower.id,
          currentUserId!!,
          onSuccess = { target, curr ->
            Log.d("ProfileViewModel", "Successfully removed follower and following")
            _currentUserProfile.value = curr
            _currentUserFollowers.value = _currentUserFollowers.value.filter { it.id != target.id }
            _currentUserFollowing.value =
                _currentUserFollowing.value.map {
                  if (it.id == target.id) {
                    target
                  } else {
                    it
                  }
                }
            if (viewingUserId == target.id) {
              _viewingUserProfile.value = target
              _viewingUserFollowing.value =
                  _viewingUserFollowing.value.filter { it.id != currentUserId }
              _viewingUserFollowers.value =
                  _viewingUserFollowers.value.map {
                    if (it.id == currentUserId) {
                      curr
                    } else {
                      it
                    }
                  }
            }
          },
          onFailure = { error ->
            _errorMessages.value = "Failed to remove follower and following: ${error.message}"
            Log.e("ProfileViewModel", "Failed to remove follower and following: ${error.message}")
          })
    }
  }

  /**
   * Updates the profile picture of the current user.
   *
   * @param profileViewModel The ProfileViewModel of the user.
   * @param picture The URI of the new profile picture.
   */
  fun updateProfilePicture(profileViewModel: ProfileViewModel, picture: Uri) {
    repository.uploadProfilePicture(
        profileViewModel = profileViewModel,
        onFailure = { throw error("Can't upload profile picture to the database") },
        uri = picture)
  }
}
