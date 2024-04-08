package com.android.feedme.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.model.CameraViewModel
import kotlinx.coroutines.launch

/**
 * A composable function representing the camera screen.
 *
 * This function displays a UI for camera functionality, allowing users to capture photos.
 * It manages camera permissions, sets up a live camera preview, and includes UI elements for
 * capturing images and viewing them in a gallery.
 * Utilizes CameraX for camera operations and Jetpack Compose for the UI components.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
  val applicationContext = LocalContext.current

  // Request camera permission if not already granted
  if (!hasRequiredPermissions(applicationContext)) {
    ActivityCompat.requestPermissions(
        applicationContext as Activity, arrayOf(Manifest.permission.CAMERA), 0)
  }

  // Set up the camera controller, view model, and coroutine scope
  val scope = rememberCoroutineScope()
  val scaffoldState = rememberBottomSheetScaffoldState()
  val controller = remember {
    LifecycleCameraController(applicationContext).apply {
      setEnabledUseCases(CameraController.IMAGE_CAPTURE)
    }
  }
  val viewModel = viewModel<CameraViewModel>()
  val bitmaps by viewModel.bitmaps.collectAsState()
  val photoSavedMessageVisible by viewModel.photoSavedMessageVisible.collectAsState()

  BottomSheetScaffold(
      modifier = Modifier.testTag("CameraScreen"),
      scaffoldState = scaffoldState,
      sheetPeekHeight = 0.dp,
      sheetContent = {
        PhotoBottomSheetContent(bitmaps = bitmaps, modifier = Modifier.fillMaxWidth())
      }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

          Row(
              modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
              horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(
                    modifier = Modifier.testTag("GalleryButton"),
                    // Open the local gallery when the gallery button is clicked
                    onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } }) {
                      Icon(imageVector = Icons.Default.Photo, contentDescription = "Open gallery")
                    }

                IconButton(
                    modifier = Modifier.testTag("PhotoButton"),
                    // Take a photo when the photo button is clicked
                    onClick = {
                      takePhoto(
                          controller = controller,
                          onPhotoTaken = viewModel::onTakePhoto,
                          showText = viewModel::onPhotoSaved,
                          context = applicationContext)
                    }) {
                      Icon(
                          imageVector = Icons.Default.PhotoCamera,
                          contentDescription = "Take photo")
                    }
              }
          // Show the message box if the photo was taken
          if (photoSavedMessageVisible) {
            Log.d("CameraScreen", "Photo saved message visible")
            // Show the message box
            Box(
                modifier =
                    Modifier.padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .align(Alignment.BottomCenter)) {
                  Text(
                      text = "Photo saved",
                      color = Color.White,
                      modifier = Modifier.testTag("PhotoSavedMessage"))
                }
          }
        }
      }
}

/** Create a new [LifecycleCameraController] to control the camera. */
fun takePhoto(
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit,
    showText: () -> Unit,
    context: Context
) {
  controller.takePicture(
      ContextCompat.getMainExecutor(context),
      object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
          super.onCaptureSuccess(image)

          // Rotate the image to match the device's orientation
          val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
          val rotatedBitmap =
              Bitmap.createBitmap(image.toBitmap(), 0, 0, image.width, image.height, matrix, true)

          onPhotoTaken(rotatedBitmap)
          showText()
        }

        // Log an error if the photo couldn't be taken
        override fun onError(exception: ImageCaptureException) {
          super.onError(exception)
          Log.e("Camera", "Couldn't take photo: ", exception)
        }
      })
}

/** Check if the app has the required permissions to use the camera. */
fun hasRequiredPermissions(context: Context): Boolean {
  return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
      PackageManager.PERMISSION_GRANTED
}

/** Composable that displays the camera preview. */
@Composable
fun CameraPreview(controller: LifecycleCameraController, modifier: Modifier = Modifier) {
  val lifecycleOwner = LocalLifecycleOwner.current
  // Display the camera preview using the CameraX PreviewView
  AndroidView(
      factory = {
        PreviewView(it).apply {
          this.controller = controller
          controller.bindToLifecycle(lifecycleOwner)
        }
      },
      modifier = modifier.testTag("CameraPreview"))
}

@Composable
fun PhotoBottomSheetContent(bitmaps: List<Bitmap>, modifier: Modifier = Modifier) {
  // Show a message if there are no photos
  if (bitmaps.isEmpty()) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
      Text(text = "There are no photos yet")
    }
  } else {
    // Display the photos in a grid
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        contentPadding = PaddingValues(16.dp),
        modifier = modifier) {
          items(bitmaps) { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Photo",
                modifier = Modifier.clip(RoundedCornerShape(10.dp)))
          }
        }
  }
}
