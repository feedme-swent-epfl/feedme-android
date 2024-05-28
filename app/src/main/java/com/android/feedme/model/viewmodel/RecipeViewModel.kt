package com.android.feedme.model.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * A class that generates the recipe view model
 *
 * This class provides the link between the Recipe database and the rest of the code. It can be used
 * in order to extract the recipe information
 */
class RecipeViewModel : ViewModel() {
  private val recipeRepository = RecipeRepository.instance
  private val profileRepository = ProfileRepository.instance

  private val _recipe = MutableStateFlow<Recipe?>(null)
  val recipe: StateFlow<Recipe?> = _recipe
  private val _picture = MutableStateFlow<Uri?>(null)
  val picture: StateFlow<Uri?> = _picture

  private val _profiles = MutableStateFlow<Map<String, Profile>>(emptyMap())
  val profiles: StateFlow<Map<String, Profile>> = _profiles

  /** Keep track of whether an error message should be shown */
  private val _errorMessageVisible = MutableStateFlow(false)
  val errorMessageVisible = _errorMessageVisible.asStateFlow()

  /**
   * A function that selects a recipe to be displayed
   *
   * @param recipe: the recipe to be displayed
   */
  fun selectRecipe(recipe: Recipe) {
    _recipe.value = recipe
  }

  fun updatePicture(uri: Uri) {
    _picture.value = uri
  }

  /**
   * A function that validates a recipe before uploading it to the database
   *
   * @params: the fields of the recipe to be uploaded
   */
  fun validateRecipe(
      title: String,
      description: String,
      ingredients: List<IngredientMetaData?>,
      steps: List<Step>,
      userid: String,
      imageUrl: String
  ): Boolean {
    if (title.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || userid.isEmpty()) {
      _errorMessageVisible.value = true
      // Launch a coroutine to hide the message after 3 seconds (3000 milliseconds)
      viewModelScope.launch {
        delay(3000)
        _errorMessageVisible.value = false
      }
      return false
    }
    _recipe.value =
        Recipe(
            "DEFAULT_ID",
            title,
            description,
            ingredients.filterNotNull(),
            steps,
            emptyList(),
            0.0,
            userid,
            imageUrl)
    return true
  }

  /**
   * A function that fetches the profile during Login
   *
   * @param id: the unique ID of the profile we want to fetch
   */
  fun fetchProfile(
      id: String,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext
  ) {
    if (id.isBlank()) return

    if (!isNetworkAvailable(context)) {
      Log.d("fetchProfile", "Offline mode, cannot fetch recipe profile")
      return
    }

    viewModelScope.launch {
      profileRepository.getProfile(
          id,
          onSuccess = { profile ->
            profile?.let {
              _profiles.value = _profiles.value.toMutableMap().apply { this[id] = it }
            }
          },
          onFailure = {
            // Handle failure
            throw error("Profile was not fetched during Login")
          })
    }
  }

  /**
   * A function that sets the recipe in the database
   *
   * @param recipe: the recipe to set in the database
   */
  fun setRecipe(
      recipe: Recipe,
      context: Context = FirebaseFirestore.getInstance().app.applicationContext
  ) {
    viewModelScope.launch {
      recipeRepository.addRecipe(
          recipe,
          _picture.value,
          context,
          onSuccess = { _recipe.value = recipe },
          onFailure = {
            // Handle failure
            throw error("Recipe could not get updated")
          })
    }
  }
}
