package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.ml.analyzeTextForIngredients
import com.android.feedme.ml.barcodeScan
import com.android.feedme.ml.extractProductInfoFromBarcode
import com.android.feedme.ml.textExtraction
import com.android.feedme.ml.textProcessing
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
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

  private val _listOfIngredientToInput = MutableStateFlow<List<IngredientMetaData>>(emptyList())
  val listOfIngredientToInput = _listOfIngredientToInput.asStateFlow()

  /** Information's to be displayed after a ML button was pressed and lead to a successful result */
  private val _informationToDisplay = MutableStateFlow<String?>(null)
  val informationToDisplay = _informationToDisplay.asStateFlow()

  /** Information's to be displayed when an error occurs * */
  private val _errorToDisplay = MutableStateFlow<String?>(null)
  val errorToDisplay = _errorToDisplay.asStateFlow()

  /** Number of ingredient to be added to the input screen after one text recognition scan * */
  private val _nbOfIngredientAdded = MutableStateFlow<Int>(0)

  /** Last photo on which ML was run * */
  private val _lastAnalyzedPhoto = MutableStateFlow<Bitmap?>(null)

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
            _errorToDisplay.value = "ERROR : No photo to analyse, please take a picture."
          }
          is PhotoState.Photo -> {
            if (photoState.bitmap != _lastAnalyzedPhoto.value) {
              _lastAnalyzedPhoto.value = photoState.bitmap
              val result = performTextRecognition(photoState.bitmap)
              if (result != null) {
                _informationToDisplay.value = result
              }
            }
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
            _errorToDisplay.value = "ERROR : No photo to analyse, please take a picture."
          }
          is PhotoState.Photo -> {
            val result = performBarCodeScanning(photoState.bitmap)
            if (result != null) {
              _informationToDisplay.value = "$result added to your ingredient list."
            }
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
  private suspend fun performBarCodeScanning(bitmap: Bitmap): String? {
    return suspendCoroutine { continuation ->
      barcodeScan(
          bitmap,
          { barcodeNumber ->
            viewModelScope.launch {
              extractProductInfoFromBarcode(
                  barcodeNumber,
                  { productInfo ->
                    if (productInfo != null) {
                      // TODO How can i create new ingredients correctly or check if they already
                      // exist ? => Sylvain PR ?
                      updateIngredientList(
                          IngredientMetaData(
                              0.0,
                              MeasureUnit.NONE,
                              Ingredient(productInfo.productName, "Default", "DefaultID")))
                      continuation.resume(productInfo.productName)
                    } else {
                      _errorToDisplay.value =
                          "Failed to extract product name from barcode, please try again."
                      continuation.resume(null)
                    }
                  },
                  {
                    _errorToDisplay.value =
                        "Failed to extract product name from barcode, please try again."
                    continuation.resume(null)
                  })
            }
          },
          {
            _errorToDisplay.value = "Failed to identify barcode, please try again."
            continuation.resume(null)
          })
    }
  }

  /**
   * Performs [textExtraction] and [textProcessing] on the provided bitmap image. This function
   * suspends the current coroutine. This function can only be called by the view model itself.
   *
   * @param bitmap The bitmap image on which text recognition will be performed.
   * @return The recognized text if successful; otherwise, an error message.
   */
  private suspend fun performTextRecognition(bitmap: Bitmap): String? {
    var counter = 0
    return suspendCoroutine { continuation ->
      textExtraction(
          bitmap,
          { text ->
            analyzeTextForIngredients(
                text,
                { ing ->
                  counter += 1
                  updateIngredientList(ing)
                },
                onSuccess = {
                  _nbOfIngredientAdded.value = counter
                  continuation.resume(
                      "${_nbOfIngredientAdded.value} ingredient(s) added to your ingredient list.")
                },
                onFailure = { e ->
                  e.message?.let {
                    Log.d("ML", it)
                    _errorToDisplay.value =
                        "Failed to extract ingredients from text, please try again."
                  }
                  continuation.resume(null)
                })
          },
          {
            _errorToDisplay.value = "Failed to identify text, please try again."
            continuation.resume(null)
          })
    }
  }

  /**
   * Updates the list of ingredients based on the provided [IngredientMetaData].
   *
   * @param ing The ingredient metadata to update the list with.
   */
  fun updateIngredientList(ing: IngredientMetaData) {
    val existingIngredient =
        _listOfIngredientToInput.value.find {
          it.ingredient.name == ing.ingredient.name
        } // Todo change to id later

    if (existingIngredient != null) {
      // If the ingredient exists, update its quantity
      val updatedQuantity = existingIngredient.quantity + ing.quantity
      val updatedIngredient = existingIngredient.copy(quantity = updatedQuantity)
      _listOfIngredientToInput.value =
          _listOfIngredientToInput.value.filterNot { it == existingIngredient }
      _listOfIngredientToInput.value += updatedIngredient
    } else {
      // If the ingredient doesn't exist, add it to the list
      _listOfIngredientToInput.value += ing
    }
  }
}
