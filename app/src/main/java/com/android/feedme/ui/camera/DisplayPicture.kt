package com.android.feedme.ui.camera

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.ViewInAr
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
 * A composable function representing the analyzing picture screen.
 *
 * This function displays a UI for analyzing functionality, allowing users to anaylze their photo.
 */
@SuppressLint("StateFlowValueCalledInComposition")
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
  val barcodeRecognitionMode = remember { mutableStateOf(true) }
  val objectLabellingMode = remember { mutableStateOf(true) }
  ///// Machine Learning Part /////

  val bitmaps by cameraViewModel.bitmaps.collectAsState()
  val analyzed by cameraViewModel.analyzed.collectAsState()

  // List containing the ingredients analyzed to display
  val listOfIngredientToInput = cameraViewModel.listOfIngredientToInput.collectAsState()

  BottomSheetScaffold(
      modifier = Modifier.testTag("DisplayPicture"),
      topBar = {
        TopBarNavigation(
            title = "Analyze Picture",
            navAction = navigationActions,
            backArrowOnClickAction = { navigationActions.navigateTo(Screen.CAMERA) })
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
                if (barcodeRecognitionMode.value) {
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
                if (objectLabellingMode.value) {
                  IconButton(
                      onClick = { cameraViewModel.imageLabellingButtonPressed() },
                      modifier =
                          Modifier.size(56.dp)
                              .background(CameraButtonsBackground, shape = CircleShape)
                              .padding(10.dp)
                              .testTag("MLObjectButton")) {
                        Icon(
                            imageVector = Icons.Filled.ViewInAr,
                            contentDescription = "Object Labelling")
                      }
                }

                // Once the photo is analyzed, we can add the ingredients and go back to the camera
                // screen
                if (analyzed) {
                  listOfIngredientToInput.value.toMutableList().forEach(cameraViewModel::updateIngredientList)
                  navigationActions.navigateTo(Screen.CAMERA)
                }
              }
        }
      }
}

/**
 * A composable function representing the display of the image to be analyzed.
 *
 * This function displays the image to be analyzed.
 */
@Composable
fun Display(bitmaps: List<Bitmap>, modifier: Modifier = Modifier) {
  if (bitmaps.isEmpty()) return
  val bitmap = bitmaps.last()
  Image(
      bitmap.asImageBitmap(),
      contentDescription = "Photo",
      modifier = modifier.testTag("ImageToAnalyze"))
}
