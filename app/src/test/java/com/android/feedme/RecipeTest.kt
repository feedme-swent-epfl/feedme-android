package com.android.feedme

import com.android.feedme.model.data.*
import com.android.feedme.model.data.Ingredient
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RecipeTest {

  private lateinit var recipeProcessor: RecipeProcessor
  private lateinit var recipes: List<Recipe>

  @Before
  fun setUp() {
    recipeProcessor = RecipeProcessor()

    // Setup test data
    val ingredientSalt = Ingredient("Salt", "Condiment", "1")
    val ingredientSugar = Ingredient("Sugar", "Sweetener", "2")

    val recipe1 =
        Recipe(
            "1",
            "Pasta",
            "Delicious pasta",
            "Boil pasta, add sauce",
            ingredients = listOf(IngredientMetaData("1 tsp", "Teaspoon", ingredientSalt)),
            steps = listOf(Step(1, "Boil water", "Fill a pot with water, boil")),
            tags = listOf("Italian", "Dinner"))

    val recipe2 =
        Recipe(
            "2",
            "Cake",
            "Sweet cake",
            "Mix ingredients, bake",
            ingredients = listOf(IngredientMetaData("2 cups", "Cup", ingredientSugar)),
            steps = listOf(Step(1, "Mix ingredients", "Mix all together")),
            tags = listOf("Dessert", "Sweet"))

    recipes = listOf(recipe1, recipe2)
  }

  @Test
  fun filterRecipesByIngredientType_condimentFiltersCorrectly() {
    // Filtering by "Condiment"
    val result = recipeProcessor.filterRecipesByIngredientType(recipes, "Condiment")
    assertEquals(1, result.size)
    assertEquals("Pasta", result.first().name)
  }

  @Test
  fun filterRecipesByIngredientType_sweetenerFiltersCorrectly() {
    // Filtering by "Sweetener"
    val result = recipeProcessor.filterRecipesByIngredientType(recipes, "Sweetener")
    assertEquals(1, result.size)
    assertEquals("Cake", result.first().name)
  }
}

class RecipeProcessor {
  fun filterRecipesByIngredientType(recipes: List<Recipe>, type: String): List<Recipe> {
    return recipes.filter { recipe -> recipe.ingredients.any { it.ingredient.type == type } }
  }
}
