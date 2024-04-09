package com.android.feedme.model.data

data class Profile(
    val id: String,
    val name: String,
    val username: String,
    val email: String,
    val description: String, // Description of the profile
    val imageUrl: String, // Image URL of the profile
    val followers: List<String>, // list of followers IDs
    val following: List<String>, // list of following IDs
    val filter: List<String>, // list of filters to be updated
    val recipeList: List<Recipe>, // list of recipe IDs
    val commentList: List<String> // list of comment IDs
)
