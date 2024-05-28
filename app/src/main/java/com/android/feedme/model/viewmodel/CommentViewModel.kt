package com.android.feedme.model.viewmodel

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
    private val _comment = MutableStateFlow<Comment?>(null)

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe

    private val _recipes = MutableStateFlow<Map<String, Recipe>>(emptyMap())
    val recipes: StateFlow<Map<String, Recipe>> = _recipes

    private val _profiles = MutableStateFlow<Map<String, Profile>>(emptyMap())
    val profiles: StateFlow<Map<String, Profile>> = _profiles

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
   * A function that add a comment in the database Overwrites the comment object given to assign it
   * a new commentId
   *
   * @param comment: the comment to set in the database
   */
  fun addComment(comment: Comment, onSuccess: () -> Unit) {
    viewModelScope.launch {
      repository.addComment(
          comment,
          onSuccess = {
            _comment.value = comment
            onSuccess()
          },
          onFailure = {
            // Handle failure
            throw error("comment could not get updated")
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
                    throw error("Profile was not fetched during Login")
                })
        }
    }

    /**
     * A function that fetches the recipe that the comment was posted on
     *
     * @param id: the unique ID of the recipe we want to fetch
     */
    fun fetchRecipe(id: String) {
        if (id.isBlank()) {
            _recipe.value = null
            return
        }

        viewModelScope.launch {
            recipeRepository.getRecipe(
                id,
                onSuccess = { recipe ->
                    recipe?.let {
                        _recipe.value = it
                    } ?: run {
                        _recipe.value = null
                    }
                },
                onFailure = {
                    throw error("Recipe was not fetched")
                })
        }
    }
}
