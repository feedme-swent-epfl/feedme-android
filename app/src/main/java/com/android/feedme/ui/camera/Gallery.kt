package com.android.feedme.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat


@Composable
fun GalleryScreen() {
  val applicationContext = LocalContext.current

  // Request access to media images permission if not already granted
  if (!hasRequiredPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
    requestPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
  }
  val imageUri = 100
  val gallery =
      Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
  gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
  ActivityCompat.startActivityForResult(applicationContext as Activity, gallery, imageUri, null)
}
