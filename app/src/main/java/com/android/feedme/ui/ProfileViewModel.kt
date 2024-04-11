package com.android.feedme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    fun fetchProfile(username: String) {
        viewModelScope.launch {
            repository.getProfile(
                username,
                onSuccess = { profile -> _profile.value = profile },
                onFailure = {
                    // Handle failure, e.g., by setting _profile.value to null or showing an error message
                })
        }
    }
}