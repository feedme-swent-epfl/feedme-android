package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecipeViewModel : ViewModel() {
  private val repository = RecipeRepository.instance
  private val _recipe = MutableStateFlow<Recipe?>(null)
  val recipe: StateFlow<Recipe?> = _recipe

  fun selectRecipe(recipe: Recipe) {
    _recipe.value = recipe
  }

  init {}

  fun fetchRecipe(id: String) {}

  fun addRecipe() {}
}
