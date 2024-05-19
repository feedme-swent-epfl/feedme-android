package com.android.feedme.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.twotone.TextFields
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.R
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.BottomIconColorSelected
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
fun CameraScreen(navigationActions: NavigationActions, inputViewModel: InputViewModel) {
  ///// Machine Learning Part /////
  // Switch off and on the text recognition functionality
  val textRecognitionMode = remember { mutableStateOf(true) }
  val barcodeRecognition = remember { mutableStateOf(true) }
  ///// Machine Learning Part /////

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
  val cameraViewModel = viewModel<CameraViewModel>()
  val bitmaps by cameraViewModel.bitmaps.collectAsState()
  val photoSavedMessageVisible by cameraViewModel.photoSavedMessageVisible.collectAsState()
  val pickImage = cameraViewModel.galleryLauncher()

  val listOfIngredientToInput = cameraViewModel.listOfIngredientToInput.collectAsState()

  val snackbarHostStateInfo = remember { SnackbarHostState() }
  val snackbarHostStateError = remember { SnackbarHostState() }

  BottomSheetScaffold(
      modifier = Modifier.testTag("CameraScreen"),
      topBar = {
        TopBarNavigation(
            title = "Camera",
            navAction = navigationActions,
            backArrowOnClickAction = {
              inputViewModel.addToList(listOfIngredientToInput.value.toMutableList())
              navigationActions.goBack()
            })
      },
      scaffoldState = scaffoldState,
      sheetPeekHeight = 0.dp,
      sheetContent = {
        PhotoBottomSheetContent(bitmaps = bitmaps, modifier = Modifier.fillMaxWidth())
      }) { padding ->
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
                // Button for text recognition
                if (textRecognitionMode.value) {
                  IconButton(
                      onClick = { cameraViewModel.textRecognitionButtonPressed() },
                      modifier =
                          Modifier.size(56.dp)
                              .background(CameraButtonsBackground, shape = CircleShape)
                              .padding(10.dp)
                              .testTag("MLTextButton")) {
                        Icon(
                            imageVector = Icons.TwoTone.TextFields,
                            contentDescription = "Display text after ML")
                      }
                }
                // Button for barcode scanner
                if (barcodeRecognition.value) {
                  val barcodeScannerPainter = painterResource(id = R.drawable.barcode_scanner)
                  IconButton(
                      onClick = { cameraViewModel.barcodeScanButtonPressed() },
                      modifier =
                          Modifier.size(56.dp)
                              .background(CameraButtonsBackground, shape = CircleShape)
                              .padding(10.dp)
                              .testTag("MLBarcodeButton")) {
                        Icon(
                            painter = barcodeScannerPainter,
                            contentDescription = "Barcode Scanner",
                            modifier = Modifier.size(25.dp),
                            tint = BottomIconColorSelected)
                      }
                }
              }
          // Show the message "Photo Saved" box if the photo was taken
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
              modifier = Modifier.align(Alignment.TopCenter),
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

/** Check if the app has the required permissions to use the camera. */
private fun hasRequiredPermissions(context: Context): Boolean {
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
