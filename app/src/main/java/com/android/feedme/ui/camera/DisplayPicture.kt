package com.android.feedme.ui.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.twotone.TextFields
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.feedme.R
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Screen
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
fun DisplayPicture(
    navigationActions: NavigationActions,
    inputViewModel: InputViewModel,
    cameraViewModel: CameraViewModel
) {
  ///// Machine Learning Part /////
  // Switch off and on the text recognition functionality
  val textRecognitionMode = remember { mutableStateOf(true) }
  val barcodeRecognition = remember { mutableStateOf(true) }
  ///// Machine Learning Part /////

  val bitmaps by cameraViewModel.bitmaps.collectAsState()

  val listOfIngredientToInput = cameraViewModel.listOfIngredientToInput.collectAsState()

  BottomSheetScaffold(
      modifier = Modifier.testTag("Display Picture"),
      topBar = {
        TopBarNavigation(
            title = "Analyze Picture",
            navAction = navigationActions,
            backArrowOnClickAction = {
              inputViewModel.addToList(listOfIngredientToInput.value.toMutableList())
              // cameraViewModel.empty()
              navigationActions.goBack()
            })
      },
      sheetPeekHeight = 0.dp,
      sheetContent = {}) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          Display(bitmaps, Modifier.fillMaxWidth())

          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .align(Alignment.BottomCenter)
                      .padding(16.dp)
                      .padding(bottom = 32.dp),
              horizontalArrangement = Arrangement.SpaceAround) {

                // Button for text recognition
                if (textRecognitionMode.value) {
                  IconButton(
                      onClick = {
                        cameraViewModel.textRecognitionButtonPressed()
                        navigationActions.navigateTo(Screen.CAMERA)
                      },
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
                      onClick = {
                        cameraViewModel.barcodeScanButtonPressed()
                        navigationActions.navigateTo(Screen.CAMERA)
                      },
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
        }
      }
}

@Composable
fun Display(bitmaps: List<Bitmap>, modifier: Modifier = Modifier) {
  if (bitmaps.isEmpty()) return
  val bitmap = bitmaps.last()
  Image(bitmap.asImageBitmap(), contentDescription = "Photo", modifier = modifier)
}
