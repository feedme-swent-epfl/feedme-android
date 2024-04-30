package com.android.feedme.model.viewmodel

import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.Recipe
import kotlinx.coroutines.flow.MutableStateFlow

class LandingPageViewModel {

    /**
     * A function that fetches the profiles of the given Ids
     *
     * @param ids: the unique IDs of the profiles we want to fetch
     * @param fetchRecipe: the MutableStateFlow that will store the fetched profiles
     */
    fun fetchRecipes(ids: List<String>, fetchRecipe: MutableStateFlow<List<Recipe>>) {
        // Check if we actually need to fetch the recipes
        val currentIds = fetchRecipe.value.map {it.id}.toSet()
    }

}