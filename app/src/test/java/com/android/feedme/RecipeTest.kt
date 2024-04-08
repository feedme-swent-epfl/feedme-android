package com.android.feedme.model.data

import org.junit.Test
import org.junit.Assert.*

class RecipeTest {

    @Test
    fun createAndRetrieveRecipeProperties() {
        // Given
        val steps = listOf(Step(1, "Mix ingredients", "Mixing"))
        val ingredients = listOf(
            IngredientMetaData(
                1.0,
                MeasureUnit.CUP,
                Ingredient("Flour", "Dry", "1")
            )
        )
        val recipe = Recipe(
            recipeId = "1",
            title = "Simple Cake",
            description = "A simple cake recipe.",
            ingredients = ingredients,
            steps = steps,
            tags = listOf("dessert", "cake"),
            time = 60.0,
            rating = 4.5,
            userid = "user123",
            difficulty = "Easy",
            imageUrl = "https://example.com/cake.jpg"
        )

        // When - Retrieving properties (implicitly in assertions)

        // Then
        assertEquals("1", recipe.recipeId)
        assertEquals("Simple Cake", recipe.title)
        assertEquals(1, recipe.steps.size)
        assertEquals("Mix ingredients", recipe.steps.first().description)
        assertEquals(MeasureUnit.CUP, recipe.ingredients.first().measure)
        assertEquals(4.5, recipe.rating, 0.0)
        // You can continue asserting other properties as needed
    }
}
