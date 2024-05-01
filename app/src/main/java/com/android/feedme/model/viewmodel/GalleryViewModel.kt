package com.android.feedme.model.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
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
      val permission =
          if (Build.VERSION.SDK_INT >= 34)
              arrayOf(
                  Manifest.permission.READ_MEDIA_IMAGES,
                  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
          else if (Build.VERSION.SDK_INT < 33) arrayOf((Manifest.permission.READ_EXTERNAL_STORAGE))
          else arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

      ActivityCompat.requestPermissions(context as Activity, permission, 0)
    }

    return rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems),
        onResult = { uris ->
          // TODO : handling the display of the gallery pictures
        })
  }
}

private fun hasRequiredPermissions(context: Context): Boolean {
  return if (Build.VERSION.SDK_INT >= 34)
      ContextCompat.checkSelfPermission(
          context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) ==
          PackageManager.PERMISSION_GRANTED ||
          ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
              PackageManager.PERMISSION_GRANTED
  else if (Build.VERSION.SDK_INT < 33)
      ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
          PackageManager.PERMISSION_GRANTED
  else
      ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
          PackageManager.PERMISSION_GRANTED
}
