package com.android.feedme.model.data

data class Ingredient(
    var name: String,
    var id: String = "NO_ID",
    val vegan: Boolean,
    val vegetarian: Boolean
)
