package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.ml.analyzeTextForIngredients
import com.android.feedme.ml.barcodeScan
import com.android.feedme.ml.bestLabel
import com.android.feedme.ml.extractProductInfoFromBarcode
import com.android.feedme.ml.labelProcessing
import com.android.feedme.ml.objectExtraction
import com.android.feedme.ml.textExtraction
import com.android.feedme.ml.textProcessing
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.data.MeasureUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

class CameraViewModel : ViewModel() {

  val ERROR_NO_BARCODE = "Failed to identify barcode, please try again."
  val ERROR_BARCODE_PRODUCT_NAME = "Failed to extract product name from barcode, please try again."
  val ERROR_NO_TEXT = "Failed to identify text, please try again."
  val ERROR_INGREDIENT_IN_TEXT = "Failed to extract ingredients from text, please try again."
  val ERROR_NO_LABEL = "Failed to found labels corresponding to this object, please try again."
  val ERROR_NO_OBJECT = "Failed to extract object to classify from this photo, please try again."
  val ERROR_NO_FOOD_LABEL = "Failed to find a suitable food label, please try again."
  val ERR_TIMEOUT = "Timeout, with object recognition."

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

  /** Keep track of whether we should switch to the analyze screen */
  private val _photoTaken = MutableStateFlow(false)
  val photoTaken = _photoTaken.asStateFlow()

  /** Keep track of whether we should switch to the camera screen */
  private val _analyzed = MutableStateFlow(false)
  val analyzed = _analyzed.asStateFlow()

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

  @Composable
  fun galleryLauncher(): ManagedActivityResultLauncher<PickVisualMediaRequest, out Any?> {
    val context = LocalContext.current

    return rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
          uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            _bitmaps.value += ImageDecoder.decodeBitmap(source)
            _lastPhoto.value = PhotoState.Photo(ImageDecoder.decodeBitmap(source))
            onPhotoSaved()
          }
        })
  }

  /**
   * This function is called when the user selects a picture in the CameraScreen. It sets the
   * [_photoTaken] state to true, which triggers the navigation to the AnalyzeScreen.
   */
  fun onPhotoSaved() {
    _photoTaken.value = true
    // Launch a coroutine to hide the message after 3 seconds (3000 milliseconds)
    viewModelScope.launch {
      delay(50)
      _photoTaken.value = false
    }
  }

  /**
   * This function is called when the user analyzed a picture in the CameraScreen. It sets the
   * [_analyzed] state to true, which triggers the navigation to the Camera.
   */
  private fun onAnalyzeDone() {
    _analyzed.value = true
    viewModelScope.launch {
      delay(50)
      _analyzed.value = false
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
            //              case won't happen as the button is disabled when there is no photo
            //              _errorToDisplay.value = ERROR_NO_PHOTO
            //            onAnalyzeDone()
          }
          is PhotoState.Photo -> {
            if (photoState.bitmap != _lastAnalyzedPhoto.value) {
              _lastAnalyzedPhoto.value = photoState.bitmap
              val result = performTextRecognition(photoState.bitmap)
              if (result != null) {
                _informationToDisplay.value = result
                onAnalyzeDone()
              }
            }
          }
        }
      }

  /**
   * This function is called when user clicks on object classification button. Then depending on the
   * state of [_lastPhoto] it will call the [performObjectLabelling] function in an other thread to
   * not block UI. Then it will update accordingly the value of [_informationToDisplay]. If no photo
   * was taken before an error message is displayed.
   */
  fun imageLabellingButtonPressed() =
      viewModelScope.launch {
        when (val photoState = _lastPhoto.value) {
          is PhotoState.NoPhoto -> {
            // _errorToDisplay.value = ERROR_NO_PHOTO will never happen
          }
          is PhotoState.Photo -> {
            val result = performObjectLabelling(photoState.bitmap)
            if (result != null) {
              _informationToDisplay.value = "$result added to your ingredient list."
              onAnalyzeDone()
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
            //              case won't happen as the button is disabled when there is no photo
            //              _errorToDisplay.value = ERROR_NO_PHOTO
            //            onAnalyzeDone()
          }
          is PhotoState.Photo -> {
            val result = performBarCodeScanning(photoState.bitmap)
            if (result != null) {
              _informationToDisplay.value = "$result added to your ingredient list."
              onAnalyzeDone()
            }
          }
        }
      }

  /**
   * Performs object labelling on the given bitmap.
   *
   * This function processes the given bitmap to detect objects and extracts labels from the
   * detected objects. If the labelling is successful, it updates the ingredient list and returns
   * the best label. If an error occurs or no labels are found, it updates the error message
   * accordingly.
   *
   * @param bitmap The bitmap of the photo to label.
   * @return A string representing the best labelling result, or null if labelling fails or no
   *   labels are found.
   */
  private suspend fun performObjectLabelling(bitmap: Bitmap): String? {
    return withTimeoutOrNull(10000L) {
      suspendCancellableCoroutine { continuation ->
        val labelList = mutableMapOf<String, Float>()
        var errorObjectOccurred = false

        objectExtraction(
            bitmap = bitmap,
            onSuccess = { detectedObjects ->
              labelList.putAll(labelProcessing(detectedObjects))
              if (labelList.isNotEmpty()) {
                val label = bestLabel(labelList)
                if (label.isNotEmpty()) {
                  updateIngredientList(
                      IngredientMetaData(
                          0.0, MeasureUnit.NONE, Ingredient(label, "NO_ID", false, false)))
                  continuation.resume(label)
                } else {
                  _errorToDisplay.value = ERROR_NO_FOOD_LABEL
                  onAnalyzeDone()
                  continuation.resume(null)
                }
              } else {
                _errorToDisplay.value = ERROR_NO_LABEL
                onAnalyzeDone()
                continuation.resume(null)
              }
            },
            onFailure = {
              _errorToDisplay.value = ERROR_NO_OBJECT
              errorObjectOccurred = true
              continuation.resume(null)
            })
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
                      updateIngredientList(
                          IngredientMetaData(
                              0.0,
                              MeasureUnit.NONE,
                              Ingredient(productInfo.productName, "NO_ID", false, false)))
                      continuation.resume(productInfo.productName)
                    } else {
                      _errorToDisplay.value = ERROR_BARCODE_PRODUCT_NAME
                      onAnalyzeDone()
                      continuation.resume(null)
                    }
                  },
                  {
                    _errorToDisplay.value = ERROR_BARCODE_PRODUCT_NAME
                    onAnalyzeDone()
                    continuation.resume(null)
                  })
            }
          },
          {
            _errorToDisplay.value = ERROR_NO_BARCODE
            onAnalyzeDone()
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
                    _errorToDisplay.value = ERROR_INGREDIENT_IN_TEXT
                    onAnalyzeDone()
                  }
                  continuation.resume(null)
                })
          },
          {
            _errorToDisplay.value = ERROR_NO_TEXT
            onAnalyzeDone()
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
    if (ing.ingredient.id != "TEST_ID") {
      IngredientsRepository.instance.getExactFilteredIngredients(
          ing.ingredient.name,
          { ingredients ->
            if (ingredients.isNotEmpty()) {
              ing.ingredient = ingredients[0]
            }
            updateIngredientInList(ing)
          },
          {
            Log.e("CameraViewModel", "Request to Database failed ", it)
            updateIngredientInList(ing)
          })
    }
    updateIngredientInList(ing)
  }
  /**
   * Updates the ingredient in the list or adds it if it doesn't exist.
   *
   * @param ing The ingredient metadata to update or add to the list.
   */
  private fun updateIngredientInList(ing: IngredientMetaData) {
    val existingIngredient =
        _listOfIngredientToInput.value.find { it.ingredient.name == ing.ingredient.name }

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

  /**
   * This function is used to empty the error and information to display after it has been
   * processed.
   */
  fun empty() {
    _errorToDisplay.value = null
    _informationToDisplay.value = null
  }

  /** This function is used to empty the list of ingredients after it has been processed. */
  fun emptyIngredients() {
    _listOfIngredientToInput.value = emptyList()
  }
}
