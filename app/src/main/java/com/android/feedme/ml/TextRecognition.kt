package com.android.feedme.ml

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material3.Text
import com.android.feedme.BuildConfig
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

/**
 * Extract text from a bitmap image using the google ML-kit.
 *
 * @param bitmap The bitmap image from which text will be extracted.
 * @param onSuccess A callback function invoked when text extraction is successful. Receives the
 *   extracted text as a parameter.
 * @param onFailure A callback function invoked when text extraction fails.
 */
fun textExtraction(
    bitmap: Bitmap,
    onSuccess: (Text) -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
  val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  val image = bitmap.let { InputImage.fromBitmap(it, 0) }
  recognizer
      .process(image)
      .addOnFailureListener { e ->
        Log.e("TextExtraction", "Failure: ${e.message}")
        onFailure(e)
      }
      .addOnSuccessListener { visionText ->
        if (visionText.textBlocks.isEmpty()) {
          Log.e("TextExtraction", "No text found in the image")
          onFailure(Exception("No text found in the image"))
        } else {
          onSuccess(visionText)
        }
      }
}

/**
 * Processes the provided [Text] object and returns a concatenated string containing the text of
 * each block.
 *
 * This function iterates through each block, line, and element in the provided [Text] object,
 * extracting their text content and bounding box information. It then returns a string containing
 * the text of each block concatenated together.
 *
 * @param text The [Text] object containing the text to be processed.
 * @return A concatenated string containing the text of each block.
 */
fun textProcessing(text: Text): String {
  var blockText = ""
  for (block in text.textBlocks) {
    blockText += block.text
  }
  return blockText
}

/**
 * Converts a string representation of a measure unit to a corresponding [MeasureUnit] enum value.
 *
 * @param unitString The string representation of the measure unit.
 * @return The corresponding [MeasureUnit] enum value.
 */
fun getMeasureUnitFromString(unitString: String): MeasureUnit {
  return when (unitString.lowercase()) {
    "teaspoon",
    "tsp",
    "teaspoons" -> MeasureUnit.TEASPOON
    "tablespoon",
    "tbsp",
    "tablespoons" -> MeasureUnit.TABLESPOON
    "cup",
    "cups" -> MeasureUnit.CUP
    "g",
    "grams",
    "gram" -> MeasureUnit.G
    "kg",
    "kilograms",
    "kilogram" -> MeasureUnit.KG
    "l",
    "liter",
    "liters" -> MeasureUnit.L
    "ml",
    "millilitre",
    "millilitres" -> MeasureUnit.ML
    else -> MeasureUnit.NONE
  }
}

/**
 * Analyzes the provided [Text] object for ingredients and invokes a callback function for each
 * ingredient found.
 *
 * @param mlText The [Text] object to be analyzed.
 * @param forIngredientFound A callback function invoked for each ingredient found. Receives the
 *   extracted ingredient metadata as a parameter.
 * @param onSuccess A callback function invoked when text analysis is successful.
 * @param onFailure A callback function invoked when text analysis fails.
 */
fun analyzeTextForIngredients(
    mlText: Text,
    forIngredientFound: (IngredientMetaData) -> Unit,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {

  val requestJson = buildRequestJson(mlText)

  val request = buildRequest(requestJson)
  val client = OkHttpClient()
  client
      .newCall(request)
      .enqueue(
          object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
              println("Failed to execute request: ${e.message}")
              onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
              val responseBody = response.body?.string()
              println("Response: $responseBody")
              try {
                parseResponse(responseBody, forIngredientFound)
                onSuccess()
              } catch (e: Exception) {
                println(e.message)
                onFailure(e)
              }
            }
          })
}

/**
 * Builds a JSON request body for sending a text analysis request.
 *
 * @param mlText The [Text] object containing the text to be analyzed.
 * @return The JSON request body as a string.
 */
fun buildRequestJson(mlText: Text): String {
  return """
        {
            "model": "gpt-3.5-turbo",
            "max_tokens": 2000,
            "messages": [{"role": "user", "content": "Your task is to extract the ingredients of a text and simplify them. Then find their respective quantity and units if they exist. Send back a jsonArray with each element having a 'ingredient', 'quantity' as Double and 'unit' If no ingredients found send an empty JSONArray. Here is the text : ${mlText.text.replace('\n', ' ')}"}]
        }
    """
      .trimIndent()
}

/**
 * Builds an HTTP request for sending a text analysis request.
 *
 * @param requestJson The JSON request body as a string.
 * @return The HTTP request object.
 */
fun buildRequest(requestJson: String): Request {
  return Request.Builder()
      .url("https://api.openai.com/v1/chat/completions")
      .post(requestJson.toRequestBody("application/json".toMediaType()))
      .header("Authorization", "Bearer ${BuildConfig.CHATGBT_API_KEY}")
      .header("Content-Type", "application/json")
      .build()
}

/**
 * Parses the response from a text analysis request and invokes a callback function for each
 * ingredient found.
 *
 * @param responseBody The response body as a JSON string.
 * @param forIngredientFound A callback function invoked for each ingredient found. Receives the
 *   extracted ingredient metadata as a parameter.
 */
fun parseResponse(responseBody: String?, forIngredientFound: (IngredientMetaData) -> Unit) {
  val jsonResponse = responseBody?.let { JSONObject(it) }
  val choicesArray = jsonResponse?.getJSONArray("choices")
  val choiceObject = choicesArray?.getJSONObject(0)
  val messageObject = choiceObject?.getJSONObject("message")
  val contentString = messageObject?.getString("content")
  val contentObject = contentString?.let { JSONArray(it) }
  // Iterate through the ingredients array
  contentObject?.let {
    for (i in 0 until contentObject.length()) {
      val ingredientObject = contentObject.getJSONObject(i)
      val ingredient = ingredientObject.optString("ingredient", "").capitalizeWords()
      val quantity = ingredientObject.getString("quantity").toDoubleOrNull() ?: 0.0
      val unitString = ingredientObject.optString("unit", "")
      val unit = getMeasureUnitFromString(unitString)
      forIngredientFound(
          IngredientMetaData(quantity, unit, Ingredient(ingredient, "NO_ID", false, false)))
    }
  }
}

/**
 * Capitalizes the first character of each word in the string.
 *
 * @return The string with each word capitalized.
 */
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
      word.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
      }
    }
