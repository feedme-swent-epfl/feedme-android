package com.android.feedme.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/** Composable function that */
@Composable
fun CreateScreen(navigationActions: NavigationActions) {

  Scaffold(
      modifier = Modifier.testTag("CreateScreen"),
      topBar = { TopBarNavigation(title = "Create", navAction = null) },
      bottomBar = {
        BottomNavigationMenu(
            selectedItem = Route.CREATE,
            onTabSelect = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS)
      },
      content = { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
          OutlinedButton(
              modifier = Modifier.padding(20.dp).testTag("CameraButton"),
              onClick = { navigationActions.navigateTo(Route.CAMERA) }) {
                Text(text = "Camera")
              }
          OutlinedButton(
              modifier = Modifier.padding(20.dp).testTag("GalleryButton"),
              onClick = { navigationActions.navigateTo(Route.GALLERY) }) {
                Text(text = "Gallery")
              }
        }
      })
}
