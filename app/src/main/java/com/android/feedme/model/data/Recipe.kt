package com.android.feedme.model.data

data class Recipe(
    var recipeId: String = "DEFAULT_ID", // Unique identifier for the recipe
    val title: String = "", // Title of the recipe
    val description: String = "", // Description of the recipe for the thumbnail
    val ingredients: List<IngredientMetaData> =
        emptyList(), // List of ingredients with quantity and measure
    val steps: List<Step> = emptyList(), // List of steps to prepare the recipe
    val tags: List<String> = emptyList(), // List of tags for the recipe
    val rating: Double = 0.0, // Rating of the recipe
    val userid: String = "", // User id of the recipe creator
    var imageUrl: String = "", // Image URL of the recipe
    var searchItems: List<String> = listOf("new"), // List of search items for the recipe
    val comments: List<String> = emptyList() ,// List of comments for the recipe
)

data class Step(
    var stepNumber: Int,
    val description: String, // description of the step
    val title: String // title of the step
)

data class IngredientMetaData(
    val quantity: Double, // Quantity of the ingredient
    val measure: MeasureUnit, // Measure unit of the ingredient
    var ingredient: Ingredient // Ingredient object
) {

  override fun toString(): String {
    return "$quantity ${measure.toString()} of ${ingredient.name}"
  }
}

enum class MeasureUnit {
  TEASPOON,
  TABLESPOON,
  CUP,
  G,
  KG,
  L,
  ML,
  NONE,
  EMPTY,
  PIECES;

  override fun toString(): String {
    return if (name == "NONE") " / " else name.lowercase()
  }
}
