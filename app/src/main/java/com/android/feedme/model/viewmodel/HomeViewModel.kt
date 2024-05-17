package com.android.feedme.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A class that generates the home view model
 *
 * This class is responsible for fetching the recipes and updating the UI with the fetched recipes
 */
class HomeViewModel : ViewModel() {

  private val recipeRepository = RecipeRepository.instance

  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes = _recipes.asStateFlow()

  private var lastRecipe: DocumentSnapshot? = null

  init {
    FirebaseAuth.getInstance().uid?.let { fetchRecipes() }
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

  /** A function that fetches the recipes */
  fun fetchRecipes() {
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
    fetchRecipes()
  }

  /**
   * A function that forces recipes to be shown for testing purposes
   *
   * @param recipes: a list of recipes to show
   */
  fun setRecipes(recipes: List<Recipe>) {
    _recipes.value = recipes
  }
}
