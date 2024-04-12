package com.android.feedme.model.data

data class Profile(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val followers: List<String> = listOf(),
    val following: List<String> = listOf(),
    val filter: List<String> = listOf(), // Setting of alergie / setting
    val recipeList: List<String> = listOf(), // Assuming this is a list of recipe IDs
    // TODO ADD recipeSave / RecipeCreated
    val commentList: List<String> = listOf()
)
