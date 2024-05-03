package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LandingPageViewModel : ViewModel() {

  private val repository = RecipeRepository.instance
  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes = _recipes.asStateFlow()

  init {
    fetchRecipe("lasagna1")
    fetchRecipe("lasagna1")
    fetchRecipe("lasagna1")
  }

  /**
   * A function that fetches the recipe during Login
   *
   * @param id: the unique ID of the recipe we want to fetch
   */
  fun fetchRecipe(id: String) {
    viewModelScope.launch {
      repository.getRecipe(
          id,
          onSuccess = { recipe ->
            if (recipe != null) {
              _recipes.value += recipe
            }
          },
          onFailure = {
            // Handle failure
            throw error("Recipe was not fetched during Login")
          })
    }
  }

  /**
   * A function that fetches the recipes given their Ids
   *
   * @param ids: the unique IDs of the profiles we want to fetch
   */
  /*fun fetchRecipes(ids: List<String>) {
    // Check if we actually need to fetch the recipes
    val currentIds = _recipes.value.map { it.recipeId }.toSet()
    if (currentIds != ids.toSet() && ids.isNotEmpty()) {
      Log.d("LandingPageViewModel", "Fetching recipes: $ids")
      viewModelScope.launch {
        repository.getRecipes(
            ids,
            onSuccess = { recipes ->
              // Avoid unnecessary updates
              if (_recipes.value != recipes) {
                _recipes.value = recipes
              } else {
                Log.d("LandingPageViewModel", "Recipes already fetched")
              }
            },
            onFailure = {
              // Handle failure
              throw error("Recipes were not fetched")
            })
      }
    } TODO : We will use this for recommendations (maybe)
  }*/
}
