package com.android.feedme.ui

import com.android.feedme.BuildConfig
import com.android.feedme.ml.buildRequest
import com.android.feedme.ml.buildRequestJson
import com.android.feedme.ml.capitalizeWords
import com.android.feedme.ml.getMeasureUnitFromString
import com.android.feedme.ml.parseResponse
import com.android.feedme.ml.textProcessing
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.google.mlkit.vision.text.Text
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class TextRecognitionTest {

  @Test
  fun testGetMeasureUnitFromString() {
    assertEquals(MeasureUnit.TEASPOON, getMeasureUnitFromString("tsp"))
    assertEquals(MeasureUnit.TABLESPOON, getMeasureUnitFromString("tablespoon"))
    assertEquals(MeasureUnit.CUP, getMeasureUnitFromString("cup"))
    assertEquals(MeasureUnit.G, getMeasureUnitFromString("g"))
    assertEquals(MeasureUnit.KG, getMeasureUnitFromString("kg"))
    assertEquals(MeasureUnit.L, getMeasureUnitFromString("l"))
    assertEquals(MeasureUnit.ML, getMeasureUnitFromString("ml"))
    assertEquals(MeasureUnit.TEASPOON, getMeasureUnitFromString("teaspoons"))
    assertEquals(MeasureUnit.TABLESPOON, getMeasureUnitFromString("tbsp"))
    assertEquals(MeasureUnit.G, getMeasureUnitFromString("grams"))
    assertEquals(MeasureUnit.KG, getMeasureUnitFromString("kilogram"))
    assertEquals(MeasureUnit.ML, getMeasureUnitFromString("millilitres"))
    // Test for unknown unit
    assertEquals(MeasureUnit.NONE, getMeasureUnitFromString("xyz"))
    // Test for case insensitivity
    assertEquals(MeasureUnit.TEASPOON, getMeasureUnitFromString("Tsp"))
    assertEquals(MeasureUnit.TABLESPOON, getMeasureUnitFromString("TableSpoon"))
    assertEquals(MeasureUnit.CUP, getMeasureUnitFromString("CUP"))
    assertEquals(MeasureUnit.G, getMeasureUnitFromString("g"))
  }

  @Test
  fun testBuildRequestJson() {
    val mockText = mock(Text::class.java)
    `when`(mockText.text).thenReturn("Test text")

    val expectedRequestJson =
        """
            {
                "model": "gpt-3.5-turbo",
                "max_tokens": 2000,
                "messages": [{"role": "user", "content": "Your task is to extract the ingredients of a text and simplify them. Then find their respective quantity and units if they exist. Send back a jsonArray with each element having a 'ingredient', 'quantity' as Double and 'unit' If no ingredients found send an empty JSONArray. Here is the text : Test text"}]
            }
        """
            .trimIndent()

    assertEquals(expectedRequestJson, buildRequestJson(mockText))
  }

  @Test
  fun testBuildRequest() {
    val mockRequestJson = "{}"
    val expectedRequest =
        Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(mockRequestJson.toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer ${BuildConfig.CHATGBT_API_KEY}")
            .header("Content-Type", "application/json")
            .build()

    assertEquals(expectedRequest.url, buildRequest(mockRequestJson).url)
    assertEquals(
        expectedRequest.header("Authorization"),
        buildRequest(mockRequestJson).header("Authorization"))
    assertEquals(
        expectedRequest.header("Content-Type"),
        buildRequest(mockRequestJson).header("Content-Type"))
  }

  @Test
  fun testCapitalizeWords_NormalCase() {
    val input = "hello world"
    val expected = "Hello World"
    assertEquals(expected, input.capitalizeWords())
  }

  @Test
  fun parseResponseWithEmptyJson() {
    val responseBody = "{}"

    val expectedResult = emptyList<IngredientMetaData>()

    val resultList = mutableListOf<IngredientMetaData>()

    parseResponse(responseBody) { ingredientMetaData -> resultList.add(ingredientMetaData) }
    assertEquals(expectedResult, resultList)
  }

  @Test
  fun testTextProcessingWithEmptyText() {
    val emptyText = mock(Text::class.java)
    `when`(emptyText.textBlocks).thenReturn(emptyList())

    val result = textProcessing(emptyText)

    // Assert that the result is an empty string
    assert(result.isEmpty())
  }

  @Test
  fun testTextProcessingWithNonEmptyText() {
    // Mocking a non-empty text with one block containing some text
    val mockBlock = mock(Text.TextBlock::class.java)
    `when`(mockBlock.text).thenReturn("This is a test block")

    val nonEmptyText = mock(Text::class.java)
    `when`(nonEmptyText.textBlocks).thenReturn(listOf(mockBlock))

    val result = textProcessing(nonEmptyText)

    // Assert that the result contains the text from the block
    assert(result == "This is a test block")
  }
}
