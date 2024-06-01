package com.android.feedme.model.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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

  private var _lastRecipe = MutableStateFlow<DocumentSnapshot?>(null)
  private var _lastProfile = MutableStateFlow<DocumentSnapshot?>(null)
  val lastRecipe = _lastRecipe.asStateFlow()
  val lastProfile = _lastProfile.asStateFlow()

  private var query: String = ""

  /**
   * A function that fetches the recipes given a query
   *
   * @param query: the query to search for in the recipes
   * @param context: the context of the application
   */
  fun searchRecipes(
      query: String,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext
  ) {
    this.query = query

    viewModelScope.launch {
      recipeRepository.getFilteredRecipes(
          query,
          context,
          _lastRecipe.value,
          onSuccess = { filteredRecipes, lastRec ->
            _lastRecipe.value = lastRec
            _filteredRecipes.value += filteredRecipes
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
  fun searchProfiles(
      query: String,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext
  ) {
    this.query = query

    viewModelScope.launch {
      profileRepository.getFilteredProfiles(
          query,
          context,
          _lastProfile.value,
          onSuccess = { filteredProfiles, lastProf ->
            _lastProfile.value = lastProf
            _filteredProfiles.value += filteredProfiles
          },
          onFailure = {
            // Handle failure
            throw error("Filtered profiles could not be fetched")
          })
    }
  }

  /**
   * A function that fetches more recipes based on the last recipe fetched This function is called
   * when the user scrolls to the bottom of the list of recipes
   *
   * @param context: the context of the application
   */
  fun loadMoreRecipes(context: Context = FirebaseFirestore.getInstance().app.applicationContext) {
    searchRecipes(query, context)
  }

  /**
   * A function that fetches more profiles based on the last profile fetched This function is called
   * when the user scrolls to the bottom of the list of profiles
   *
   * @param context: the context of the application
   */
  fun loadMoreProfiles(context: Context = FirebaseFirestore.getInstance().app.applicationContext) {
    searchProfiles(query, context)
  }

  /** A function that resets the filtered recipes and profiles lists */
  fun resetSearch() {
    _filteredRecipes.value = emptyList()
    _filteredProfiles.value = emptyList()
    _lastRecipe.value = null
    _lastProfile.value = null
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
