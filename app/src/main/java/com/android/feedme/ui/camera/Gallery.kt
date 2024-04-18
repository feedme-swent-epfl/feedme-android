package com.android.feedme.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.android.feedme.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(navigationActions: NavigationActions) {
  val applicationContext = LocalContext.current

  // Request access to media images permission if not already granted
  if (!hasRequiredPermission(applicationContext, Manifest.permission.READ_MEDIA_IMAGES)) {
    requestPermission(applicationContext, Manifest.permission.READ_MEDIA_IMAGES)
  }
  val imageUri = 100
  val gallery =
      Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
  ActivityCompat.startActivityForResult(applicationContext as Activity, gallery, imageUri, null)
}
