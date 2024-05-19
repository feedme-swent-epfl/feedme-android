package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A class that generates the recipe view model
 *
 * This class provides the link between the Recipe database and the rest of the code. It can be used
 * in order to extract the recipe information
 */
class RecipeViewModel : ViewModel() {
  private val repository = RecipeRepository.instance
  private val _recipe = MutableStateFlow<Recipe?>(null)
  val recipe: StateFlow<Recipe?> = _recipe
  /** Keep track of whether an error message should be shown */
  private val _errorMessageVisible = MutableStateFlow<Boolean>(false)
  val errorMessageVisible = _errorMessageVisible.asStateFlow()

  /**
   * A function that selects a recipe to be displayed
   *
   * @param recipe: the recipe to be displayed
   */
  fun selectRecipe(recipe: Recipe) {
    _recipe.value = recipe
  }

  /**
   * A function that validates a recipe before uploading it to the database
   *
   * @param recipe: the recipe to be displayed
   */
  fun validateRecipe(
      title: String,
      description: String,
      ingredients: List<IngredientMetaData?>,
      steps: List<Step>,
      userid: String,
      imageUrl: String
  ): Boolean {
    if (title.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || userid.isEmpty()) {
      _errorMessageVisible.value = true
      // Launch a coroutine to hide the message after 3 seconds (3000 milliseconds)
      viewModelScope.launch {
        delay(3000)
        _errorMessageVisible.value = false
      }
      return false
    }
    _recipe.value =
        Recipe(
            "DEFAULT_ID",
            title,
            description,
            ingredients.filterNotNull(),
            steps,
            emptyList(),
            0.0,
            userid,
            imageUrl)
    return true
  }

  /**
   * A function that sets the recipe in the database
   *
   * @param recipe: the recipe to set in the database
   */
  fun setRecipe(recipe: Recipe) {
    viewModelScope.launch {
      repository.addRecipeTest(
          recipe,
          onSuccess = { _recipe.value = recipe },
          onFailure = {
            // Handle failure
            throw error("Recipe could not get updated")
          })
    }
  }
}
