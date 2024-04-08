package com.android.feedme.model.data

data class Recipe(
    val recipeId: String,
    val name: String,
    val shortDescription: String,
    val detailedDescription: String,
    val ingredients: List<IngredientMetaData>,
    val steps: List<Step>,
    val tags: List<String>,
)

data class Step(
    val stepNumber: Int,
    val shortDescription: String,
    val detailedDescription: String,
)

data class IngredientMetaData(
    val quantity: String,
    val measure: String,
    val ingredient: Ingredient
)
