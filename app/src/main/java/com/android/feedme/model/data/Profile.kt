package com.android.feedme.model.data

data class Profile(
    val id: String = "DEFAULT_ID",
    val name: String = "John Doe",
    val username: String = "defaultusername",
    val email: String = "johndoe@gmail.com",
    val description: String = "No Bio Yet",
    val imageUrl: String = "jajaj",
    val followers: List<String> = listOf(),
    val following: List<String> = listOf(),
    val filter: List<String> = listOf(), // Setting of alergie / setting
    val recipeList: List<String> = listOf(), // Assuming this is a list of recipe IDs
    // TODO ADD recipeSave / RecipeCreated
    val commentList: List<String> = listOf()
)
