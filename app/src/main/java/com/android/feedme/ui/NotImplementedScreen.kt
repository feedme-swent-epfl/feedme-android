package com.android.feedme.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * Screen that is displayed when a feature is not implemented.
 *
 * @param navigationActions The navigation actions instance for handling back navigation.
 */
@Composable
fun NotImplementedScreen(navigationActions: NavigationActions, selectedItem: String) {

  Scaffold(
      modifier = Modifier.testTag("NotImplementedScreen"),
      topBar = { TopBarNavigation(title = "Not Implemented", navAction = null) },
      bottomBar = {
        BottomNavigationMenu(
            selectedItem = selectedItem,
            onTabSelect = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS)
      },
      content = { paddingValues ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(paddingValues)) {
              Text(
                  text = "This feature is not implemented yet. Please check back later.",
                  modifier = Modifier.padding(20.dp).testTag("Text"))
            }
      })
}
