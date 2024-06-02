package com.android.feedme.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.CameraButtonsBackground

/**
 * A composable function representing the camera screen.
 *
 * This function displays a UI for camera functionality, allowing users to capture photos. It
 * manages camera permissions, sets up a live camera preview, and includes UI elements for capturing
 * images and viewing them in a gallery. Utilizes CameraX for camera operations and Jetpack Compose
 * for the UI components.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(navigationActions: NavigationActions, cameraViewModel: CameraViewModel) {

  val applicationContext = LocalContext.current
  // Request camera permission if not already granted
  if (!hasRequiredPermissions(applicationContext)) {
    ActivityCompat.requestPermissions(
        applicationContext as Activity, arrayOf(Manifest.permission.CAMERA), 0)
  }

  val scaffoldState = rememberBottomSheetScaffoldState()
  val controller = remember {
    LifecycleCameraController(applicationContext).apply {
      setEnabledUseCases(CameraController.IMAGE_CAPTURE)
    }
  }
  val photoTaken by cameraViewModel.photoTaken.collectAsState()
  val pickImage = cameraViewModel.galleryLauncher(null, null, null)

  val snackbarHostStateInfo = remember { SnackbarHostState() }
  val snackbarHostStateError = remember { SnackbarHostState() }

  BottomSheetScaffold(
      modifier = Modifier.testTag("CameraScreen"),
      topBar = {
        TopBarNavigation(
            title = "Camera",
            navAction = navigationActions,
            backArrowOnClickAction = {
              cameraViewModel.empty()
              navigationActions.navigateTo(Screen.FIND_RECIPE)
            })
      },
      scaffoldState = scaffoldState,
      sheetPeekHeight = 0.dp,
      sheetContent = {}) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .align(Alignment.BottomCenter)
                      .padding(16.dp)
                      .padding(bottom = 32.dp),
              horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(
                    modifier =
                        Modifier.size(56.dp)
                            .background(CameraButtonsBackground, shape = CircleShape)
                            .padding(10.dp)
                            .testTag("GalleryButton"),
                    // Open the local gallery when the gallery button is clicked
                    onClick = {
                      pickImage.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                      Icon(imageVector = Icons.Default.Photo, contentDescription = "Open gallery")
                    }

                IconButton(
                    modifier =
                        Modifier.size(56.dp)
                            .background(CameraButtonsBackground, shape = CircleShape)
                            .padding(10.dp)
                            .testTag("PhotoButton"),
                    // Take a photo when the photo button is clicked
                    onClick = {
                      takePhoto(
                          controller = controller,
                          onPhotoTaken = cameraViewModel::onTakePhoto,
                          showText = cameraViewModel::onPhotoSaved,
                          context = applicationContext,
                      )
                    }) {
                      Icon(
                          imageVector = Icons.Default.PhotoCamera,
                          contentDescription = "Take photo")
                    }
              }
          // Switch to the analyze picture screen
          if (photoTaken) {
            cameraViewModel.empty()
            navigationActions.navigateTo(Screen.ANALYZE_PICTURE)
          }
          // Snack bar host for info messages (green)
          SnackbarHost(
              hostState = snackbarHostStateInfo,
              modifier = Modifier.align(Alignment.TopCenter),
              snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = Color.Green.copy(alpha = 0.5f),
                    contentColor = Color.White)
              })

          // Snack bar host for error messages (red)
          SnackbarHost(
              hostState = snackbarHostStateError,
              modifier = Modifier.align(Alignment.TopCenter).testTag("Snack error"),
              snackbar = { snackbarData ->
                Snackbar(
                    modifier = Modifier.testTag("Error Snack Bar"),
                    snackbarData = snackbarData,
                    containerColor = Color.Red.copy(alpha = 0.5f),
                    contentColor = Color.White)
              })

          // Information snack bar is displayed each time there is something new to display from the
          // view model
          LaunchedEffect(Unit) {
            cameraViewModel.informationToDisplay.collect {
              Log.d("Snack bar", "Snack bar information")
              if (it != null) {
                snackbarHostStateInfo.showSnackbar(message = it, duration = SnackbarDuration.Short)
              }
            }
          }
          // Error snack bar is displayed each time an error related to ML occurs
          LaunchedEffect(Unit) {
            cameraViewModel.errorToDisplay.collect {
              Log.d("Snack bar", "Snack bar error")
              if (it != null) {
                snackbarHostStateError.showSnackbar(message = it, duration = SnackbarDuration.Short)
              }
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

/** Check if the app has the required permissions to use the camera and the gallery. */
private fun hasRequiredPermissions(context: Context): Boolean {
  return if (Build.VERSION.SDK_INT >= 34)
      ContextCompat.checkSelfPermission(
          context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) ==
          PackageManager.PERMISSION_GRANTED ||
          ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
              PackageManager.PERMISSION_GRANTED &&
              ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                  PackageManager.PERMISSION_GRANTED
  else if (Build.VERSION.SDK_INT < 33)
      ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
          PackageManager.PERMISSION_GRANTED &&
          ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
              PackageManager.PERMISSION_GRANTED
  else
      ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
          PackageManager.PERMISSION_GRANTED &&
          ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
              PackageManager.PERMISSION_GRANTED
}

/** Asks the required permissions to use the camera and the gallery. */
private fun askForPermission(context: Context) {
  val permission =
      if (Build.VERSION.SDK_INT >= 34)
          arrayOf(
              Manifest.permission.READ_MEDIA_IMAGES,
              Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
      else if (Build.VERSION.SDK_INT < 33) arrayOf((Manifest.permission.READ_EXTERNAL_STORAGE))
      else arrayOf(Manifest.permission.READ_MEDIA_IMAGES)

  return ActivityCompat.requestPermissions(context as Activity, permission, 0)
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
