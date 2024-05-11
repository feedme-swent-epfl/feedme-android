package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

  private val recipeRepository = RecipeRepository.instance
  private val profileRepository = ProfileRepository.instance

  private val _recommendedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  private val _filteredRecipes = MutableStateFlow<List<Recipe>>(emptyList())

  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes = _recipes.asStateFlow()

  private val _filteredProfiles = MutableStateFlow<List<Profile>>(emptyList())
  val filteredProfiles = _filteredProfiles.asStateFlow()

  var initialSearchQuery = ""

  init {
    FirebaseAuth.getInstance().uid?.let {
      fetchRecipe("lasagna1")
      fetchRecipe("lasagna1")
      fetchRecipe("lasagna1")
    }
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
              _recommendedRecipes.value += recipe
              _recipes.value = _recommendedRecipes.value
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
          onSuccess = { filteredRecipes ->
            _filteredRecipes.value = filteredRecipes
            _recipes.value = filteredRecipes
          },
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

  /** A function that resets the filtered recipes and profiles lists */
  fun resetSearch() {
    _filteredRecipes.value = emptyList()
    _filteredProfiles.value = emptyList()
    _recipes.value = _recommendedRecipes.value
  }

  /**
   * A function that sets which recipes to show
   *
   * @param isFiltered: a boolean that indicates if the filtered recipes should be shown
   */
  fun setShowedRecipes(isFiltered: Boolean) {
    _recipes.value =
        if (isFiltered) {
          _filteredRecipes.value
        } else {
          _recommendedRecipes.value
        }
  }

  /**
   * A function that forces recipes to be shown for testing purposes
   *
   * @param recipes: a list of recipes to show
   * @param isFiltered: a boolean that indicates if the filtered recipes should be shown
   */
  fun setRecipes(recipes: List<Recipe>, isFiltered: Boolean = false) {
    if (isFiltered) {
      _filteredRecipes.value = recipes
    } else {
      _recommendedRecipes.value = recipes
    }
  }
}
