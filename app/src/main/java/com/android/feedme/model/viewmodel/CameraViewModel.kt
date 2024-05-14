package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.BuildConfig
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject

class CameraViewModel : ViewModel() {

  // Keep a list of bitmaps taken by the user
  private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  val bitmaps = _bitmaps.asStateFlow()

  // Keep track of whether the photo saved message should be shown
  private val _photoSavedMessageVisible = MutableStateFlow<Boolean>(false)
  val photoSavedMessageVisible = _photoSavedMessageVisible.asStateFlow()

  // Contains the last photo taken by user
  var lastPhoto: Bitmap? by mutableStateOf(null)

  val listOfIngredient: List<Ingredient> =
      listOf(Ingredient("Tomate", "DEFAULT_TYPE", "DEFAULT_ID"))

  private val _listOfIngredientToInput = MutableStateFlow<List<IngredientMetaData>>(emptyList())
  val listOfIngredientToInput = _listOfIngredientToInput.asStateFlow()

  /**
   * This function is called when the user taps the photo button in the CameraScreen. It adds the
   * bitmap to the list of bitmaps in the _bitmaps state.
   */
  fun onTakePhoto(bitmap: Bitmap) {
    _bitmaps.value += bitmap
    lastPhoto = bitmap
  }

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

  fun analyzeTextForIngredients(
      mlText: Text,
      onSuccess: () -> Unit = {},
      onFailure: (Exception) -> Unit = {}
  ) {

    val requestJson =
        """
    {
        "model": "gpt-3.5-turbo",
        "max_tokens": 2000,
        "messages": [{"role": "user", "content": "Your task is to extract the ingredients of a text and simplify them. Then find their respective quantity and units if they exist. Send back a jsonArray with each element having a 'ingredient', 'quantity' as Double and 'unit' If no ingredients found send an empty JSONArray. Here is the text : ${mlText.text.replace('\n', ' ')}"}]
    }
    """
            .trimIndent()

    val request =
        Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestJson.toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer ${BuildConfig.CHATGBT_API_KEY}")
            .header("Content-Type", "application/json")
            .build()

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
                  val jsonResponse = responseBody?.let { JSONObject(it) }
                  val choicesArray = jsonResponse?.getJSONArray("choices")
                  val choiceObject = choicesArray?.getJSONObject(0)
                  val messageObject = choiceObject?.getJSONObject("message")
                  val contentString = messageObject?.getString("content")
                  val contentObject = contentString?.let { JSONArray(it) }

                  // Iterate through the ingredients array
                  if (contentObject != null) {
                    for (i in 0 until contentObject.length()) {
                      val ingredientObject = contentObject.getJSONObject(i)
                      val ingredient = ingredientObject.optString("ingredient", "")
                      val quantity = ingredientObject.getString("quantity").toDoubleOrNull() ?: 0.0
                      val unitString = ingredientObject.optString("unit", "")
                      val unit = getMeasureUnitFromString(unitString)

                      _listOfIngredientToInput.value +=
                          IngredientMetaData(
                              quantity,
                              unit,
                              Ingredient(ingredient, "DEFAULT_TYPE", "DEFAULT_ID"),
                          )
                    }
                  }
                  onSuccess()
                } catch (e: Exception) {
                  println(e.message)
                  onFailure(e)
                }
              }
            })
  }

  /**
   * This function is called when the user taps the save button in the CameraScreen. It sets the
   * _photoSavedMessageVisible state to true, which triggers a message to be shown to the user. The
   * message is hidden after 3 seconds.
   */
  fun onPhotoSaved() {
    _photoSavedMessageVisible.value = true

    // Launch a coroutine to hide the message after 3 seconds (3000 milliseconds)
    viewModelScope.launch {
      delay(3000)
      _photoSavedMessageVisible.value = false
    }
  }
}
