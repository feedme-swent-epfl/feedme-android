package com.android.feedme.model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

  /**
   * This function is called when the user taps the photo button in the CameraScreen. It adds the
   * bitmap to the list of bitmaps in the _bitmaps state.
   */
  fun onTakePhoto(bitmap: Bitmap) {
    _bitmaps.value += bitmap
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
