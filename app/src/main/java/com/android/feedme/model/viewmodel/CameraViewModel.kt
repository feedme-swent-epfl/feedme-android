package com.android.feedme.model.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.feedme.ml.TextProcessing
import com.android.feedme.ml.TextRecognition
import com.google.common.base.Optional
import com.google.mlkit.vision.text.Text
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

  fun TextRecognitionButtonPressed() {
    when (val photoState = _lastPhoto.value) {
      is PhotoState.NoPhoto -> {
        // Handle the case where there is no photo available
        _InformationToDisplay.value = "ERROR : No photo to analyse, please take a picture."
      }
      is PhotoState.Photo -> {
        // Handle the case where a photo is available
        var extractedText : Text? = null
        TextRecognition(photoState.bitmap) { t -> extractedText = t }
        if (extractedText != null) {
          _InformationToDisplay.value = TextProcessing(text = extractedText!!)
        } else {
          _InformationToDisplay.value = "ERROR : Failed to identify text, please try again."
        }
      }
    }
  }
}
