package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Comment
import com.android.feedme.model.data.CommentRepository
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
  private val _comment = MutableStateFlow<Comment?>(null)

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
   * A function that sets the comment in the database
   *
   * @param comment: the comment to set in the database
   */
  fun setComment(comment: Comment) {
    viewModelScope.launch {
      repository.addComment(
          comment,
          onSuccess = { _comment.value = comment },
          onFailure = {
            // Handle failure
            throw error("comment could not get updated")
          })
    }
  }

    /**
     * A function that sets the comment in the database
     *
     * @param comment: the comment to set in the database
     */
    fun addComment(comment: Comment,  onSuccess: () -> Unit ) {
        viewModelScope.launch {
            repository.addComment(
                comment,
                onSuccess = { _comment.value = comment
                            onSuccess()
                            },
                onFailure = {
                    // Handle failure
                    throw error("comment could not get updated")
                })
        }
    }
}
