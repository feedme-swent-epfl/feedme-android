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
  private val _currentUserFollowers = MutableStateFlow<List<Profile>>(listOf())
  private val _currentUserFollowing = MutableStateFlow<List<Profile>>(listOf())
  val currentUserFollowers: StateFlow<List<Profile>> = _currentUserFollowers
  val currentUserFollowing: StateFlow<List<Profile>> = _currentUserFollowing

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
                fetchProfiles(profile.followers, _currentUserFollowers)
                fetchProfiles(profile.following, _currentUserFollowing)
              }
            },
            onFailure = {
              // Handle failure
              throw error("Profile was not fetched during Login")
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
            } else {
              updateViewingUserProfile(profile)
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
  private fun updateCurrentUserProfile(profile: Profile) {
    _currentUserProfile.value = profile
    currentUserId = profile.id
    fetchProfiles(profile.followers, _currentUserFollowers)
    fetchProfiles(profile.following, _currentUserFollowing)
  }

  /**
   * Updates the viewing user's profile.
   *
   * @param profile The updated profile.
   */
  private fun updateViewingUserProfile(profile: Profile) {
    _viewingUserProfile.value = profile
    viewingUserId = profile.id
    fetchProfiles(profile.followers, _viewingUserFollowers)
    fetchProfiles(profile.following, _viewingUserFollowing)
  }

  /**
   * Starts following another user, updating both the current user's following list and the other
   * user's followers list. This method is transactional, ensuring that both operations succeed or
   * fail together.
   *
   * @param currentUserId The ID of the user doing the following.
   * @param targetUserId The ID of the user being followed.
   * @throws Exception If the current user ID is null. Should never happen.
   * @throws Exception If the current user ID is the same as the target user ID. Should never
   *   happen.
   *     @throws Exception If the target user is already a follower of the current user. Should
   *       never
   */
  fun followUser(targetUser: Profile) {
      Log.d("ProfileViewModel", "Follow user called")
      Log.d("ProfileViewModel", "Current user ID: $currentUserId")
      Log.d("ProfileViewModel", "Viewing user ID: $viewingUserId")
      Log.d("ProfileViewModel", "Target user ID: ${targetUser.id}")
        Log.d("ProfileViewModel", "Target user followers: $targetUser")

    val targetUserId = targetUser.id
    val currentUserId =
        currentUserId ?: throw Exception("Current user ID is null. Should never happen.")
    if (currentUserId == targetUserId) {
      throw Exception("Current user ID is the same as the target user ID. Should never happen.")
    }
    if (targetUser.followers.contains(currentUserId)) {
      throw Exception("Target user is already a follower of the current user. Should never happen.")
    }

    viewModelScope.launch {
      repository.followUser(
          currentUserId,
          targetUserId,
          onSuccess = {
            Log.d("ProfileViewModel", "Successfully started following the user")
              targetUser.followers += currentUserId
              currentUserProfile.value!!.followers  += targetUserId
                _currentUserFollowers.value += targetUser

              if (viewingUserId == targetUserId) {
                viewingUserProfile.value!!.followers += currentUserId
                _viewingUserFollowers.value += currentUserProfile.value!!
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
   * @param targetUserId The ID of the user being unfollowed.
   * @throws Exception If the current user ID is null. Should never happen.
   * @throws Exception If the current user ID is the same as the target user ID. Should never
   *   happen.
   *     @throws Exception If the target user is not a follower of the current user. Should never
   */
  fun unfollowUser(targetUser: Profile) {
      Log.d("ProfileViewModel", "Unfollow user called")
      Log.d("ProfileViewModel", "Current user ID: $currentUserId")
      Log.d("ProfileViewModel", "Viewing user ID: $viewingUserId")
      Log.d("ProfileViewModel", "Target user ID: ${targetUser.id}")
      Log.d("ProfileViewModel", "Target user followers: $targetUser")
    val targetUserId = targetUser.id
    val requestFromUserId =
        currentUserId ?: throw Exception("Current user ID is null. Should never happen.")

    if (requestFromUserId == targetUserId) {
      throw Exception("Current user ID is the same as the target user ID. Should never happen.")
    }
    if (!targetUser.followers.contains(requestFromUserId)) {
      throw Exception("Target user is not a follower of the current user. Should never happen.")
    }

    viewModelScope.launch {
      repository.unfollowUser(
          requestFromUserId,
          targetUserId,
          onSuccess = {
            Log.d("ProfileViewModel", "Successfully unfollowed the user")
                targetUser.followers -= requestFromUserId
                currentUserProfile.value!!.followers -= targetUserId
                _currentUserFollowers.value = _currentUserFollowers.value.toMutableList().apply { remove(targetUser) }
                if (viewingUserId == targetUserId) {
                    viewingUserProfile.value!!.followers -= requestFromUserId
                    _viewingUserFollowers.value = _viewingUserFollowers.value.toMutableList().apply { remove(currentUserProfile.value!!) }
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
   * @param requestFromUserId The ID of the user whose followers list will be updated.
   * @param followerId The ID of the follower to remove.
   * @throws Exception If the current user ID is null. Should never happen.
   * @throws Exception If the current user ID is the same as the follower ID. Should never happen.
   * @throws Exception If the follower is not in the current user's following list. Should never
   */
  fun removeFollower(follower: Profile) {
    Log.d("ProfileViewModel", "Removing follower and following CALLED")
    val followerId = follower.id
    val requestFromUserId =
        currentUserId ?: throw Exception("Current user ID is null. Should never happen.")
    if (requestFromUserId == followerId) {
      throw Exception("Current user ID is the same as the follower ID. Should never happen.")
    }
    if (!follower.following.contains(requestFromUserId)) {
      throw Exception("Current user is not following the follower. Should never happen.")
    }

    viewModelScope.launch {
      repository.removeFollower(
          requestFromUserId,
          followerId,
          onSuccess = {
            Log.d("ProfileViewModel", "Successfully removed follower and following")
               // TODO Update local state if needed
                val updatedFollowers = _currentUserFollowers.value.toMutableList().apply { remove(follower) }
                _currentUserFollowers.value = updatedFollowers
                _currentUserProfile.value =
                    _currentUserProfile.value?.copy(followers = updatedFollowers.map { it.id })

              if (viewingUserId == followerId) {
                val updatedFollowing = _viewingUserFollowing.value.toMutableList().apply { remove(follower) }
                _viewingUserFollowing.value = updatedFollowing
                _viewingUserProfile.value =
                    _viewingUserProfile.value?.copy(following = updatedFollowing.map { it.id })
              }
          },
          onFailure = { error ->
            _errorMessages.value = "Failed to remove follower and following: ${error.message}"
            Log.e("ProfileViewModel", "Failed to remove follower and following: ${error.message}")
          })
    }
  }
}
