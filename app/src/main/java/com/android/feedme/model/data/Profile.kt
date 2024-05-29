package com.android.feedme.model.data

data class Profile(
    val id: String = "ID_DEFAULT",
    val name: String = "NAME_DEFAULT",
    val username: String = "USERNAME_DEFAULT",
    val email: String = "EMAIL_DEFAULT",
    val description: String = "BIO_DEFAULT",
    val imageUrl: String = "URL_DEFAULT",
    var followers: List<String> = listOf(),
    var following: List<String> = listOf(),
    val filter: List<String> = listOf(), // Setting of allergies / setting
    var recipeList: List<String> = listOf(), // Assuming this is a list of recipe IDs
    var savedRecipes: List<String> = listOf(), // Assuming this is a list of recipe IDs
    var commentList: List<String> = listOf(),
    var showDialog: Boolean = true, // Shows or hides the pop-up in the generation of recipes
)
