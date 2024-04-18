package com.android.feedme.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.app.ActivityCompat
import com.android.feedme.ui.navigation.TopBarNavigation

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
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("Gallery"),
      topBar = { TopBarNavigation(title = "Gallery") },
      content = { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center) {
              Text(text = "Not implemented yet :)")
            }
      })
}
