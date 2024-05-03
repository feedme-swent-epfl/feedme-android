package com.android.feedme.ui.camera

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.model.viewmodel.GalleryViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * A composable function representing the login screen.
 *
 * This function provides a UI for users to sign in with Google authentication. It includes a Google
 * sign-in button and handles authentication flow using Firebase authentication.
 *
 * @param navigationActions : the nav actions given in the MainActivity
 */
@Composable
fun GalleryScreen(navigationActions: NavigationActions, maxItems: Int) {
  val galleryViewModel = viewModel<GalleryViewModel>()
  val pickImage = galleryViewModel.galleryLauncher(null, maxItems)

  // Creating an intermediate screen with a button to add a level of indirection, needed to launch
  // the PickImage activity
  Scaffold(
      modifier = Modifier.testTag("GalleryScreen"),
      topBar = { TopBarNavigation(title = "Select pictures", navAction = navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            Route.FIND_RECIPE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center) {
              OutlinedButton(
                  modifier = Modifier.testTag("GalleryButton"),
                  onClick = {
                    pickImage.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                  }) {
                    Text(text = "Access gallery")
                  }
            }
      })
}
