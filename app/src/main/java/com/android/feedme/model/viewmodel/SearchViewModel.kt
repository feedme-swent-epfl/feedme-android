package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.firestore.DocumentSnapshot
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

  private var lastRecipe: DocumentSnapshot? = null
  private var lastProfile: DocumentSnapshot? = null
  private var query: String = ""

  /**
   * A function that fetches the recipes given a query
   *
   * @param query: the query to search for in the recipes
   */
  fun searchRecipes(query: String) {
    this.query = query
    viewModelScope.launch {
      recipeRepository.getFilteredRecipes(
          query,
          lastRecipe,
          onSuccess = { filteredRecipes, lastRec ->
            lastRecipe = lastRec
            // Don't re-add the same recipes if there are no new ones
            if (lastProfile != null && _filteredRecipes.value.isNotEmpty() ||
                lastProfile == null && _filteredRecipes.value.isEmpty()) {
              _filteredRecipes.value += filteredRecipes
            }
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
    this.query = query
    viewModelScope.launch {
      profileRepository.getFilteredProfiles(
          query,
          lastProfile,
          onSuccess = { filteredProfiles, lastProf ->
            lastProfile = lastProf
            _filteredProfiles.value += filteredProfiles
          },
          onFailure = {
            // Handle failure
            throw error("Filtered profiles could not be fetched")
          })
    }
  }

  /**
   * A function that fetches more recipes based on the last recipe fetched
   *
   * This function is called when the user scrolls to the bottom of the list of recipes
   */
  fun loadMoreRecipes() {
    searchRecipes(query)
  }

  /**
   * A function that fetches more profiles based on the last profile fetched
   *
   * This function is called when the user scrolls to the bottom of the list of profiles
   */
  fun loadMoreProfiles() {
    searchProfiles(query)
  }

  /** A function that resets the filtered recipes and profiles lists */
  fun resetSearch() {
    _filteredRecipes.value = emptyList()
    _filteredProfiles.value = emptyList()
    lastRecipe = null
    lastProfile = null
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
