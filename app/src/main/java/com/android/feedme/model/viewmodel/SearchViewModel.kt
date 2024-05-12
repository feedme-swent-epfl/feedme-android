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

/**
 * A class that generates the search view model
 *
 * This class is responsible for fetching the recipes and profiles based on a search query and
 * updating the UI with the filtered recipes and profiles
 */
class SearchViewModel : ViewModel() {
  private val recipeRepository = RecipeRepository.instance
  private val profileRepository = ProfileRepository.instance

  private val _filteredRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  val filteredRecipes = _filteredRecipes.asStateFlow()

  private val _filteredProfiles = MutableStateFlow<List<Profile>>(emptyList())
  val filteredProfiles = _filteredProfiles.asStateFlow()

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

  /** A function that resets the filtered recipes and profiles lists */
  fun resetSearch() {
    _filteredRecipes.value = emptyList()
    _filteredProfiles.value = emptyList()
  }

  /**
   * A function that sets the filtered recipes list for testing
   *
   * @param recipes: the list of recipes to be displayed
   */
  fun setFilteredRecipes(recipes: List<Recipe>) {
    _filteredRecipes.value = recipes
  }

  /**
   * A function that sets the filtered profiles list for testing
   *
   * @param profiles: the list of profiles to be displayed
   */
  fun setFilteredProfiles(profiles: List<Profile>) {
    _filteredProfiles.value = profiles
  }
}
