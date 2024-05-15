package com.android.feedme.model.data

data class Recipe(
    val recipeId: String = "", // Unique identifier for the recipe
    val title: String = "", // Title of the recipe
    val description: String = "", // Description of the recipe for the thumbnail
    val ingredients: List<IngredientMetaData> =
        emptyList(), // List of ingredients with quantity and measure
    val steps: List<Step> = emptyList(), // List of steps to prepare the recipe
    val tags: List<String> = emptyList(), // List of tags for the recipe
    val rating: Double = 0.0, // Rating of the recipe
    val userid: String = "", // User id of the recipe creator
    val imageUrl: String = "" // Image URL of the recipe
)

data class Step(
    var stepNumber: Int,
    val description: String, // description of the step
    val title: String // title of the step
)

data class IngredientMetaData(
    val quantity: Double, // Quantity of the ingredient
    val measure: MeasureUnit, // Measure unit of the ingredient
    val ingredient: Ingredient // Ingredient object
)

enum class MeasureUnit {
  TEASPOON,
  TABLESPOON,
  CUP,
  G,
  KG,
  L,
  ML,
  NONE,
  EMPTY
  // Add more units as needed
}
