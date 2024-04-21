package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.RecipeRepository

class RecipeViewModel : ViewModel() {
    private val repository = RecipeRepository.instance


    init {

    }

    fun fetchRecipe(id: String) {

    }

    fun addRecipe() {

    }
}