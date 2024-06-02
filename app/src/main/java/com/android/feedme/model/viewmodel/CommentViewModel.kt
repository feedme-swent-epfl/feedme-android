package com.android.feedme.model.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A class that generates the comment view model
 *
 * This class provides the link between the comment database and the rest of the code. It can be
 * used in order to extract the comment information
 */
class CommentViewModel : ViewModel() {
  private val recipeRepository = RecipeRepository.instance
  private val profileRepository = ProfileRepository.instance

  private val repository = CommentRepository.instance
  private val repositoryRecipe = RecipeRepository.instance
  private val repositoryProfile = ProfileRepository.instance
  private val _comment = MutableStateFlow<Comment?>(null)
  val comment: StateFlow<Comment?> = _comment
  private val _recipe = MutableStateFlow<Recipe?>(null)
  val recipe: StateFlow<Recipe?> = _recipe
  private val _profiles = MutableStateFlow<Map<String, Profile>>(emptyMap())
  val profiles: StateFlow<Map<String, Profile>> = _profiles
  private val _picture = MutableStateFlow<Uri?>(null)
  val picture: StateFlow<Uri?> = _picture

  /**
   * A function that selects a comment to be displayed
   *
   * @param comment: the comment to be displayed
   */
  fun selectComment(comment: Comment) {
    _comment.value = comment
  }

  /**
   * A function that stores our comment's picture
   *
   * @param comment: the comment to be displayed
   */
  fun updatePicture(uri: Uri) {
    _picture.value = uri
  }

  /**
   * A function that add a comment in the database Overwrites the comment object given to assign it
   * a new commentId
   *
   * @param comment: the comment to set in the database
   */
  fun addComment(comment: Comment) {
    viewModelScope.launch {
      repository.addComment(
          comment,
          _picture.value,
          onSuccess = {
            _comment.value = comment

            repositoryRecipe.addCommentToRecipe(
                comment.recipeId,
                comment.commentId,
                onSuccess = {
                  repositoryProfile.addCommentToProfile(
                      comment.userId,
                      comment.commentId,
                      onSuccess = {},
                      onFailure = {
                        // Handle failure
                      })
                },
                onFailure = {
                  // Handle failure
                })
          },
          onFailure = {
            // Handle failure
          })
    }
  }

  /**
   * A function that gets a comment from the database
   *
   * @param comment: the comment to get from the database
   */
  fun getComment(comment: Comment, onSuccess: () -> Unit) {
    viewModelScope.launch {
      repository.getComment(
          comment.commentId,
          onSuccess = {
            _comment.value = comment
            onSuccess()
          },
          onFailure = {
            // Handle failure
          })
    }
  }

  /**
   * A function that fetches the profile during Login
   *
   * @param id: the unique ID of the profile we want to fetch
   */
  fun fetchProfile(id: String) {
    if (id.isBlank()) {
      return
    }

    viewModelScope.launch {
      profileRepository.getProfile(
          id,
          onSuccess = { profile ->
            profile?.let {
              _profiles.value = _profiles.value.toMutableMap().apply { this[id] = it }
            }
          },
          onFailure = {
            // Handle failure
          })
    }
  }
}
