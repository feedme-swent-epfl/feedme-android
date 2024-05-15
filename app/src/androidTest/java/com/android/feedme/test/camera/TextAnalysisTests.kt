package com.android.feedme.test.camera

import com.android.feedme.ml.analyzeTextForIngredients
import com.android.feedme.ml.parseResponse
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.google.mlkit.vision.text.Text
import io.mockk.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.json.JSONObject
import org.junit.Test

class TextAnalysisTests {

  @Test
  fun testAnalyzeTextForIngredients() {
    // Mock the Text object
    val mockText = mockk<Text>()
    every { mockText.text } returns "1 cup flour, 2 eggs, 1/2 teaspoon salt"

    // Capture the callback arguments
    val ingredientsList = mutableListOf<IngredientMetaData>()
    val forIngredientFound: (IngredientMetaData) -> Unit = { ingredientsList.add(it) }

    // Create a CountDownLatch to wait for the asynchronous operation
    val latch = CountDownLatch(1)

    // Call the function under test
    analyzeTextForIngredients(
        mockText,
        forIngredientFound,
        onSuccess = { latch.countDown() },
        onFailure = { latch.countDown() })

    // Wait for the asynchronous operation to complete
    latch.await(5, TimeUnit.SECONDS)

    // Verify the captured arguments
    assert(ingredientsList.size == 3)
    assert(
        ingredientsList[0].quantity == 1.0 &&
            ingredientsList[0].measure == MeasureUnit.CUP &&
            ingredientsList[0].ingredient.name == "Flour")
    assert(
        ingredientsList[1].quantity == 2.0 &&
            ingredientsList[1].measure == MeasureUnit.NONE &&
            ingredientsList[1].ingredient.name == "Eggs")
    assert(
        ingredientsList[2].quantity == 0.5 &&
            ingredientsList[2].measure == MeasureUnit.TEASPOON &&
            ingredientsList[2].ingredient.name == "Salt")
  }

  @Test
  fun testParseResponse() {
    // Create a mock JSON response
    val jsonResponse =
        JSONObject(
            """
            {
                "choices": [
                    {
                        "message": {
                            "content": "[{\"ingredient\":\"flour\",\"quantity\":\"1\",\"unit\":\"cup\"},{\"ingredient\":\"eggs\",\"quantity\":\"2\",\"unit\":\"\"},{\"ingredient\":\"salt\",\"quantity\":\"0.5\",\"unit\":\"teaspoon\"}]"
                        }
                    }
                ]
            }
        """
                .trimIndent())

    // Capture the callback arguments
    val ingredientsList = mutableListOf<IngredientMetaData>()
    val forIngredientFound: (IngredientMetaData) -> Unit = { ingredientsList.add(it) }

    // Call the function under test
    parseResponse(jsonResponse.toString(), forIngredientFound)

    // Verify the captured arguments
    assert(ingredientsList.size == 3)
    assert(
        ingredientsList[0].quantity == 1.0 &&
            ingredientsList[0].measure == MeasureUnit.CUP &&
            ingredientsList[0].ingredient.name == "Flour")
    assert(
        ingredientsList[1].quantity == 2.0 &&
            ingredientsList[1].measure == MeasureUnit.NONE &&
            ingredientsList[1].ingredient.name == "Eggs")
    assert(
        ingredientsList[2].quantity == 0.5 &&
            ingredientsList[2].measure == MeasureUnit.TEASPOON &&
            ingredientsList[2].ingredient.name == "Salt")
  }
}
