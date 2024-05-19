package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    // TODO : is the check complete?
    if (title.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || userid.isEmpty())
        return false
    _recipe.value =
        Recipe(
            "",
            title,
            description ?: "",
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
      repository.addRecipe(
          recipe,
          onSuccess = { _recipe.value = recipe },
          onFailure = {
            // Handle failure
            throw error("Recipe could not get updated")
          })
    }
  }
}
