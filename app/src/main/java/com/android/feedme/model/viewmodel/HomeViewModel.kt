package com.android.feedme.model.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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

  private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  val savedRecipes = _savedRecipes.asStateFlow()

  private var lastRecipe: DocumentSnapshot? = null

  private val authListener =
      FirebaseAuth.AuthStateListener { FirebaseAuth.getInstance().uid?.let { fetchRatedRecipes() } }

  init {
    // Listen to FirebaseAuth state changes
    FirebaseAuth.getInstance().addAuthStateListener(authListener)
  }

  /**
   * A function that fetches the recipe during Login
   *
   * @param id: the unique ID of the recipe we want to fetch
   * @param context: the context of the application
   */
  fun fetchRecipe(
      id: String,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext
  ) {
    viewModelScope.launch {
      recipeRepository.getRecipe(
          id,
          context,
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
   * @param context: the context of the application
   */
  fun fetchSavedRecipes(
      ids: List<String>,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext
  ) {
    viewModelScope.launch {
      val recipeList = mutableListOf<Recipe>()
      recipeRepository.getSavedRecipes(
          context,
          ids,
          onSuccess = { recipe, _ ->
            recipeList += recipe
            _savedRecipes.value = recipeList
          },
          onFailure = {
            // Handle failure
            Log.d("fetchRecipe", "Failed fetch of $ids")
          })
    }
  }

  /**
   * A function that fetches the recipes based on their ratings
   *
   * @param context: the context of the application
   */
  fun fetchRatedRecipes(context: Context = FirebaseFirestore.getInstance().app.applicationContext) {
    viewModelScope.launch {
      recipeRepository.getRatedRecipes(
          context,
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

  /**
   * A function that fetches more recipes
   *
   * @param context: the context of the application
   */
  fun loadMoreRecipes(context: Context = FirebaseFirestore.getInstance().app.applicationContext) {
    fetchRatedRecipes(context)
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
