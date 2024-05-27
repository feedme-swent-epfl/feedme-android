package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A class that generates the view model for the generate screen
 *
 * This class provides the link between the generate screen and the rest of the code. It can be used
 * in order to generate the recipes
 */
class GenerateViewModel : ViewModel() {
  private val recipeRepository = RecipeRepository.instance

  private val _generatedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  val generatedRecipes = _generatedRecipes.asStateFlow()

  private var lastRecipe: DocumentSnapshot? = null
  private var _ingredientsIds: List<String> = emptyList()
  private var _profile: Profile = Profile()

  private val _isStrict = MutableStateFlow(false)

  /**
   * A function that fetches the generated recipes
   *
   * @param ingredientIds: the unique IDs of the recipes we want to fetch
   * @param profile: the profile of the user
   */
  fun fetchGeneratedRecipes(ingredientIds: List<String>, profile: Profile) {
    _ingredientsIds = ingredientIds
    _profile = profile

    if (_isStrict.value) {
      recipeRepository.suggestRecipesStrict(
          ingredientIds,
          profile,
          lastRecipe,
          onSuccess = { recipes, lastRec ->
            lastRecipe = lastRec
            _generatedRecipes.value = recipes
          },
          onFailure = {
            // Handle failure
          })
    } else {
      recipeRepository.suggestRecipes(
          ingredientIds,
          profile,
          lastRecipe,
          onSuccess = { recipes, lastRec ->
            lastRecipe = lastRec
            _generatedRecipes.value = recipes
          },
          onFailure = {
            // Handle failure
          })
    }
  }

  /** A function that fetches more of the generated recipes */
  fun loadMoreGeneratedRecipes() {
    fetchGeneratedRecipes(_ingredientsIds, _profile)
  }

  /**
   * A function that toggles the strictness of the generated recipes
   *
   * @param isStrict: the strictness of the generated recipes
   */
  fun toggleStrictness(isStrict: Boolean) {
    _isStrict.value = isStrict
  }

  /**
   * A function that sets the recipes for testing purposes
   *
   * @param recipes: the recipes to set
   */
  fun setRecipes(recipes: List<Recipe>) {
    _generatedRecipes.value = recipes
  }
}
