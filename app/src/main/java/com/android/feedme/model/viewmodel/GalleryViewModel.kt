package com.android.feedme.model.viewmodel

import android.Manifest
import android.app.Activity
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.android.feedme.ui.camera.hasRequiredPermissions
import kotlinx.coroutines.flow.MutableStateFlow

class GalleryViewModel : ViewModel() {

  private val _uris = MutableStateFlow<List<Uri>>(emptyList())

  @Composable
  fun galleryLauncher(
      maxItems: Int
  ): ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>> {
    val context = LocalContext.current
    val maxImages = if (maxItems < 1) 1 else maxItems

    if (!hasRequiredPermissions(context)) {
      ActivityCompat.requestPermissions(
          context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
    }

    return rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems),
        onResult = { uris ->
          // TODO : handling the display of the gallery pictures
        })
  }
}
