package com.android.feedme.model.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
import com.android.feedme.model.data.ProfileRepository
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
  private val repository = CommentRepository.instance
  private val repositoryRecipe = RecipeRepository.instance
  private val repositoryProfile = ProfileRepository.instance
  private val _comment = MutableStateFlow<Comment?>(null)

  private val _picture = MutableStateFlow<Uri?>(null)
  val picture: StateFlow<Uri?> = _picture

  val comment: StateFlow<Comment?> = _comment

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
  fun addComment(comment: Comment, onSuccess: () -> Unit) {
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
                      onSuccess = { onSuccess() },
                      onFailure = {
                        // Handle failure

                      })
                  onSuccess()
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
}
