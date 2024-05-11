package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

  private val recipeRepository = RecipeRepository.instance
  private val profileRepository = ProfileRepository.instance

  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes = _recipes.asStateFlow()

  private val _filteredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  val filteredRecipes = _filteredRecipes.asStateFlow()

  private val _filteredProfiles = MutableStateFlow<List<Profile>>(emptyList())
  val filteredProfiles = _filteredProfiles.asStateFlow()

  var initialSearchQuery = ""

    var isFiltered: Boolean = false

  init {
    /*FirebaseAuth.getInstance().uid?.let {
      fetchRecipe("lasagna1")
      fetchRecipe("lasagna1")
      fetchRecipe("lasagna1")
    }*/
  }

  /**
   * A function that fetches the recipe during Login
   *
   * @param id: the unique ID of the recipe we want to fetch
   */
  fun fetchRecipe(id: String) {
    viewModelScope.launch {
      recipeRepository.getRecipe(
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

  /**
   * A function that fetches the recipes given a query
   *
   * @param query: the query to search for in the recipes
   */
  fun searchRecipes(query: String) {
    viewModelScope.launch {
      recipeRepository.getFilteredRecipes(
          query,
          onSuccess = { filteredRecipes -> _filteredRecipes.value = filteredRecipes },
          onFailure = {
            // Handle failure
            throw error("Filtered recipes could not be fetched")
          })
    }
  }

  /**
   * A function that fetches the profiles given a query
   *
   * @param query: the query to search for in the profiles
   */
  fun searchProfiles(query: String) {
    viewModelScope.launch {
      profileRepository.getFilteredProfiles(
          query,
          onSuccess = { filteredProfiles -> _filteredProfiles.value = filteredProfiles },
          onFailure = {
            // Handle failure
            throw error("Filtered profiles could not be fetched")
          })
    }
  }

  fun resetSearch() {
    _filteredRecipes.value = emptyList()
    _filteredProfiles.value = emptyList()
  }

  fun setRecipes(recipes: List<Recipe>, filtered: Boolean = false) {
    _recipes.value = recipes
  }

}
