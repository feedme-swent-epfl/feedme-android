package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.ml.analyzeTextForIngredients
import com.android.feedme.ml.barcodeScan
import com.android.feedme.ml.extractProductNameFromBarcode
import com.android.feedme.ml.textExtraction
import com.android.feedme.ml.textProcessing
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {

  /** This sealed class is used to model the different states that a photo can be in. */
  sealed class PhotoState() {

    /** Represents the state when there is no photo available. */
    object NoPhoto : PhotoState()

    /**
     * Represents the state when a photo is available.
     *
     * @property bitmap The bitmap image associated with the photo state.
     */
    data class Photo(val bitmap: Bitmap) : PhotoState()
  }

  /** Keep a list of bitmaps taken by user */
  private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  val bitmaps = _bitmaps.asStateFlow()

  /** Keep track of whether the photo saved message should be shown */
  private val _photoSavedMessageVisible = MutableStateFlow<Boolean>(false)
  val photoSavedMessageVisible = _photoSavedMessageVisible.asStateFlow()

  /** Contains the last photo taken by user */
  private val _lastPhoto = MutableStateFlow<PhotoState>(PhotoState.NoPhoto)
  // Could be useful later if lastPhoto needs to be accessed outside the view model
  val lastPhoto = _lastPhoto.asStateFlow()

  val listOfIngredient: List<Ingredient> =
      listOf(Ingredient("Tomate", "DEFAULT_TYPE", "DEFAULT_ID"))

  private val _listOfIngredientToInput = MutableStateFlow<List<IngredientMetaData>>(emptyList())
  val listOfIngredientToInput = _listOfIngredientToInput.asStateFlow()

  /** Information's to be displayed after a ML button was pressed */
  private val _informationToDisplay = MutableStateFlow<String>("")
  val informationToDisplay = _informationToDisplay.asStateFlow()

  /**
   * This function is called when the user taps the photo button in the CameraScreen. It adds the
   * bitmap to the list of bitmaps in the [_bitmaps] state and updates the [_lastPhoto].
   */
  fun onTakePhoto(bitmap: Bitmap) {
    _bitmaps.value += bitmap
    _lastPhoto.value = PhotoState.Photo(bitmap)
  }

  /**
   * This function is called when the user taps the save button in the CameraScreen. It sets the
   * [_photoSavedMessageVisible] state to true, which triggers a message to be shown to the user.
   * The message is hidden after 3 seconds.
   */
  fun onPhotoSaved() {
    _photoSavedMessageVisible.value = true
    // Launch a coroutine to hide the message after 3 seconds (3000 milliseconds)
    viewModelScope.launch {
      delay(3000)
      _photoSavedMessageVisible.value = false
    }
  }

  /**
   * This function is called when user clicks on text recognition button. Then depending on the
   * state of [_lastPhoto] it will call the [performTextRecognition] function in an other thread to
   * not block UI. Then it will update accordingly the value of [_informationToDisplay]. If no photo
   * was taken before an error message is displayed.
   */
  fun textRecognitionButtonPressed() =
      viewModelScope.launch {
        when (val photoState = _lastPhoto.value) {
          is PhotoState.NoPhoto -> {
            _informationToDisplay.value = "ERROR : No photo to analyse, please take a picture."
          }
          is PhotoState.Photo -> {
            val result = performTextRecognition(photoState.bitmap)
            _informationToDisplay.value = result
          }
        }
      }

  /**
   * This function is called when user clicks on barcode scanning button. Then depending on the
   * state of [_lastPhoto] it will call the [performBarCodeScanning] function in an other thread to
   * not block UI. Then it will update accordingly the value of [_informationToDisplay]. If no photo
   * was taken before an error message is displayed.
   */
  fun barcodeScanButtonPressed() =
      viewModelScope.launch {
        when (val photoState = _lastPhoto.value) {
          is PhotoState.NoPhoto -> {
            _informationToDisplay.value = "ERROR : No photo to analyse, please take a picture."
          }
          is PhotoState.Photo -> {
            val result = performBarCodeScanning(photoState.bitmap)
            _informationToDisplay.value = result
          }
        }
      }

  /**
   * Performs [barcodeScan] and [extractProductNameFromBarcode] on the provided bitmap image. This
   * function suspends the current coroutine. This function can only be called by the view model
   * itself.
   *
   * @param bitmap The bitmap image on which barcode scanning will be performed.
   * @return The extracted product name from the barcode as string if successful, a relevant error
   *   message otherwise.
   */
  private suspend fun performBarCodeScanning(bitmap: Bitmap): String {
    return suspendCoroutine { continuation ->
      barcodeScan(
          bitmap,
          { barcodeNumber ->
            viewModelScope.launch {
              extractProductNameFromBarcode(
                  barcodeNumber,
                  { productName -> continuation.resume(productName) },
                  {
                    continuation.resume(
                        "ERROR: Failed to extract product name from barcode, please try again.")
                  })
            }
          },
          { continuation.resume("ERROR: Failed to identify barcode, please try again.") })
    }
  }

  /**
   * Performs [textExtraction] and [textProcessing] on the provided bitmap image. This function
   * suspends the current coroutine. This function can only be called by the view model itself.
   *
   * @param bitmap The bitmap image on which text recognition will be performed.
   * @return The recognized text if successful; otherwise, an error message.
   */
  private suspend fun performTextRecognition(bitmap: Bitmap): String {
    return suspendCoroutine { continuation ->
      textExtraction(
          bitmap,
          { text ->
            analyzeTextForIngredients(
                text,
                { ing ->
                  val existingIngredient =
                      _listOfIngredientToInput.value.find { it.ingredient.id == ing.ingredient.id }

                  if (existingIngredient != null) {
                    // If the ingredient exists, update its quantity
                    val updatedQuantity = existingIngredient.quantity + ing.quantity
                    val updatedIngredient = existingIngredient.copy(quantity = updatedQuantity)
                    _listOfIngredientToInput.value += updatedIngredient
                  } else {
                    // If the ingredient doesn't exist, add it to the list
                    _listOfIngredientToInput.value += ing
                  }
                })
            continuation.resume(textProcessing(text = text))
          },
          { continuation.resume("ERROR : Failed to identify text, please try again.") })
    }
  }
}
