package com.android.feedme.model.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
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

  val bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  val _uris = MutableStateFlow<List<Uri>>(emptyList())
  val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  private val _uris = MutableStateFlow<Set<Uri>>(setOf())

  @Composable
  fun galleryLauncher(
      profileViewModel: ProfileViewModel?,
      maxItems: Int
  ): ManagedActivityResultLauncher<PickVisualMediaRequest, out Any?> {
    val context = LocalContext.current

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

    if (maxItems <= 1) {
      return rememberLauncherForActivityResult(
          ActivityResultContracts.PickVisualMedia(),
          onResult = { uri ->
            uri?.let { profileViewModel?.updateProfilePicture(profileViewModel, uri) }
          })
    } else {
      return rememberLauncherForActivityResult(
          ActivityResultContracts.PickMultipleVisualMedia(maxItems),
          onResult = { uris ->
            uris.let {
              for (uri in it) {
                _uris.value += uri
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                bitmaps.value += bitmap
              }
            }
          })
    }
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
