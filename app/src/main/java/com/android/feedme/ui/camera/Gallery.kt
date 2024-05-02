package com.android.feedme.ui.camera

import android.annotation.SuppressLint
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.model.viewmodel.GalleryViewModel
import com.android.feedme.ui.navigation.NavigationActions

/**
 * A composable function representing the login screen.
 *
 * This function provides a UI for users to sign in with Google authentication. It includes a Google
 * sign-in button and handles authentication flow using Firebase authentication.
 *
 * @param navigationActions : the nav actions given in the MainActivity
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun GalleryScreen(navigationActions: NavigationActions, maxItems: Int) {
  val galleryViewModel = viewModel<GalleryViewModel>()
  val pickImage = galleryViewModel.galleryLauncher(maxItems)
  val bitmaps by galleryViewModel.bitmaps.collectAsState()
  // Creating an intermediate screen with a button to add a level of indirection, needed to launch
  // the PickImage activity
  /*    Column {
      Box(
          modifier = Modifier
              .fillMaxSize(),
          contentAlignment = Alignment.Center
      ) {
          OutlinedButton(
              modifier = Modifier.testTag("GalleryButton"),
              onClick = {
                  pickImage.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
              }) {
              Text(text = "Select pictures")
          }
      }
      if (bitmaps.isEmpty()) {
          Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
              Text(text = "There are no photos yet")
          }
      } else {
          // Display the photos in a grid
          LazyVerticalStaggeredGrid(
              columns = StaggeredGridCells.Fixed(2),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalItemSpacing = 16.dp,
              contentPadding = PaddingValues(16.dp),
              modifier = Modifier.fillMaxWidth()) {
              items(bitmaps) { bitmap ->
                  Image(
                      bitmap = bitmap.asImageBitmap(),
                      contentDescription = "Photo",
                      modifier = Modifier.clip(RoundedCornerShape(10.dp)))
              }
          }
      }
  }*/
  Column {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
      OutlinedButton(
          modifier = Modifier.testTag("GalleryButton"),
          onClick = {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          }) {
            Text(text = "Select pictures")
          }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("GalleryScreen"),
        /*topBar = { TopBarNavigation(title = "Select from Gallery", navAction = navigationActions) },
        bottomBar = {
            BottomNavigationMenu(
                Route.FIND_RECIPE,
                { top ->
                    navigationActions.navigateTo(top)
                },
                TOP_LEVEL_DESTINATIONS)
        },*/
        content = { innerPadding ->
          // Column(modifier = Modifier.padding(innerPadding)) {
          if (bitmaps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center) {
                  Text(text = "There are no photos yet")
                }
          } else {
            // Display the photos in a grid
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()) {
                  items(bitmaps) { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Photo",
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)))
                  }
                }
          }
          // }
        })
  }
  /*
      Scaffold(
          modifier = Modifier.fillMaxSize().testTag("GalleryScreen"),
          topBar = { TopBarNavigation(title = "Select from Gallery", navAction = navigationActions) },
          bottomBar = {
              BottomNavigationMenu(
                  Route.FIND_RECIPE,
                  { top ->
                      navigationActions.navigateTo(top)
                  },
                  TOP_LEVEL_DESTINATIONS)
          },
          content = { innerPadding ->
              Column(modifier = Modifier.padding(innerPadding)) {
                  if (bitmaps.isEmpty()) {
                      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                          Text(text = "There are no photos yet")
                      }
                  } else {
                      // Display the photos in a grid
                      LazyVerticalStaggeredGrid(
                          columns = StaggeredGridCells.Fixed(2),
                          horizontalArrangement = Arrangement.spacedBy(16.dp),
                          verticalItemSpacing = 16.dp,
                          contentPadding = PaddingValues(16.dp),
                          modifier = Modifier.fillMaxWidth()) {
                          items(bitmaps) { bitmap ->
                              Image(
                                  bitmap = bitmap.asImageBitmap(),
                                  contentDescription = "Photo",
                                  modifier = Modifier.clip(RoundedCornerShape(10.dp)))
                          }
                      }
                  }
              }
              Box(
                  modifier = Modifier
                      .fillMaxSize(),
                  contentAlignment = Alignment.Center
              ) {
                  OutlinedButton(
                      modifier = Modifier.testTag("GalleryButton"),
                      onClick = {
                          pickImage.launch(
                              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                          )
                      }) {
                      Text(text = "Select pictures")
                  }
              }

          })
  */

}
