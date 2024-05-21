package com.android.feedme.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

  private val recipeRepository = RecipeRepository.instance

  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes = _recipes.asStateFlow()

  private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  val savedRecipes = _savedRecipes.asStateFlow()

  private val authListener =
<<<<<<< Updated upstream
      FirebaseAuth.AuthStateListener {
        FirebaseAuth.getInstance().uid?.let {
          fetchRecipe("lasagna1")
          fetchRecipe("pasta1")
        }
      }
=======
      FirebaseAuth.AuthStateListener { FirebaseAuth.getInstance().uid?.let { fetchRatedRecipes() } }
>>>>>>> Stashed changes

  init {
    // Listen to FirebaseAuth state changes
    FirebaseAuth.getInstance().addAuthStateListener(authListener)
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
            Log.d("fetchRecipe", "Failed fetch of $id")
          })
    }
  }

  /**
   * A function that fetches the recipe during Login
   *
   * @param ids: the unique ID of the recipe we want to fetch
   */
  fun fetchSavedRecipes(ids: List<String>) {
    viewModelScope.launch {
      val recipeList = mutableListOf<Recipe>()
      recipeRepository.getRecipes(
          ids,
          onSuccess = { recipe ->
            recipeList += recipe
            _savedRecipes.value = recipeList
          },
          onFailure = {
            // Handle failure
            Log.d("fetchRecipe", "Failed fetch of $ids")
          })
    }
  }

<<<<<<< Updated upstream
=======
  /** A function that fetches the recipes */
  fun fetchRatedRecipes() {
    viewModelScope.launch {
      recipeRepository.getRatedRecipes(
          lastRecipe,
          onSuccess = { recipes, lastRec ->
            lastRecipe = lastRec
            _recipes.value += recipes
          },
          onFailure = {
            // Handle failure
            throw error("Recipes could not be fetched")
          })
    }
  }

  /** A function that fetches more recipes */
  fun loadMoreRecipes() {
    fetchRatedRecipes()
  }

>>>>>>> Stashed changes
  /**
   * A function that forces recipes to be shown for testing purposes
   *
   * @param recipes: a list of recipes to show
   */
  fun setRecipes(recipes: List<Recipe>) {
    _recipes.value = recipes
  }

  /**
   * A function that forces saved recipes to be shown for testing purposes
   *
   * @param recipes: a list of recipes to show
   */
  fun setSavedRecipes(recipes: List<Recipe>) {
    _savedRecipes.value = recipes
  }
}
