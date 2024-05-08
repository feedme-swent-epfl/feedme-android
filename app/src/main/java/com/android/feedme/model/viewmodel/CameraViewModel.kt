package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.google.mlkit.vision.text.Text
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {

  // Keep a list of bitmaps taken by the user
  private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  val bitmaps = _bitmaps.asStateFlow()

  // Keep track of whether the photo saved message should be shown
  private val _photoSavedMessageVisible = MutableStateFlow<Boolean>(false)
  val photoSavedMessageVisible = _photoSavedMessageVisible.asStateFlow()

  // Contains the last photo taken by user
  var lastPhoto: Bitmap? by mutableStateOf(null)

  val listOfIngredient: List<Ingredient> = listOf()

  /**
   * This function is called when the user taps the photo button in the CameraScreen. It adds the
   * bitmap to the list of bitmaps in the _bitmaps state.
   */
  fun onTakePhoto(bitmap: Bitmap) {
    _bitmaps.value += bitmap
    lastPhoto = bitmap
  }

  fun detectIngredientsFromText(text: Text): List<IngredientMetaData> {
    val ingredientsMetaDataDetected = mutableListOf<IngredientMetaData>()

    // Iterate through the detected text blocks
    text.textBlocks.forEach { block ->
      // Iterate through the text lines
      block.lines.forEach { line ->
        // Split the line into words
        val words = line.text.split("\\s+".toRegex())

        // Check for patterns indicating ingredient, quantity, and measure unit
        for (i in 0 until words.size - 1) {
          val word = words[i]
          val nextWord = words[i + 1]
          if (word.matches(Regex("\\d+(\\.\\d+)?"))) {
            // Current word is a number, check if next word is an ingredient
            if (!nextWord.matches(Regex("\\d+(\\.\\d+)?"))) {
              // Next word is not a number, consider it as ingredient
              val quantity = word.toDouble()
              val ingredientName = nextWord.lowercase()
              val measureUnit = parseMeasureUnit(words[i + 2])

              // Find the Ingredient object from the list of known ingredients
              val ingredient =
                  listOfIngredient.find { it.name.equals(ingredientName, ignoreCase = true) }
              if (ingredient != null) {
                // Ingredient found in the list, create IngredientMetaData and add it to the
                // ingredients map
                val ingredientMetaData =
                    IngredientMetaData(
                        quantity = quantity, measure = measureUnit, ingredient = ingredient)
                ingredientsMetaDataDetected.add(ingredientMetaData)
              }
            }
          }
        }
      }
    }
    return ingredientsMetaDataDetected
  }

  private fun parseMeasureUnit(unit: String): MeasureUnit {
    return when (unit.lowercase()) {
      "teaspoon" -> MeasureUnit.TEASPOON
      "tablespoon" -> MeasureUnit.TABLESPOON
      "cup" -> MeasureUnit.CUP
      "g" -> MeasureUnit.G
      "kg" -> MeasureUnit.KG
      "l" -> MeasureUnit.L
      "ml" -> MeasureUnit.ML
      else -> MeasureUnit.NONE
    }
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
