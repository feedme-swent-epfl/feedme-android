package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.ml.textExtraction
import com.android.feedme.ml.textProcessing
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
  sealed class PhotoState() {
    object NoPhoto : PhotoState()

    data class Photo(val bitmap: Bitmap) : PhotoState()
  }

  // Keep a list of bitmaps taken by the user
  private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  val bitmaps = _bitmaps.asStateFlow()

  // Keep track of whether the photo saved message should be shown
  private val _photoSavedMessageVisible = MutableStateFlow<Boolean>(false)
  val photoSavedMessageVisible = _photoSavedMessageVisible.asStateFlow()

  // Contains the last photo taken by user
  private val _lastPhoto = MutableStateFlow<PhotoState>(PhotoState.NoPhoto)
  val lastPhoto = _lastPhoto.asStateFlow()

  private val _InformationToDisplay = MutableStateFlow<String>("")
  val InformationToDisplay = _InformationToDisplay.asStateFlow()

  /**
   * This function is called when the user taps the photo button in the CameraScreen. It adds the
   * bitmap to the list of bitmaps in the _bitmaps state.
   */
  fun onTakePhoto(bitmap: Bitmap) {
    _bitmaps.value += bitmap
    _lastPhoto.value = PhotoState.Photo(bitmap)
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
  /**
   * This function is called when user clicks on text recognition button. Then depending on the
   * state of [_lastPhoto] it will call the [performTextRecognition] function in an other thread to
   * not block UI. Then it will update accordingly the value of [_InformationToDisplay].
   */
  fun textRecognitionButtonPressed() =
      viewModelScope.launch {
        when (val photoState = _lastPhoto.value) {
          is PhotoState.NoPhoto -> {
            _InformationToDisplay.value = "ERROR : No photo to analyse, please take a picture."
          }
          is PhotoState.Photo -> {
            val result = performTextRecognition(photoState.bitmap)
            _InformationToDisplay.value = result
          }
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
          { text -> continuation.resume(textProcessing(text = text)) },
          { continuation.resume("ERROR : Failed to identify text, please try again.") })
    }
  }
}
