package com.android.feedme.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

@Composable
fun SavedRecipesScreen(
    navigationActions: NavigationActions,
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("SavedScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.SAVED, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("SavedScreenBox"),
            contentAlignment = Alignment.Center) {
              Text(
                  fontWeight = FontWeight(400),
                  fontSize = 20.sp,
                  color = com.android.feedme.ui.theme.TemplateColor,
                  textAlign = TextAlign.Center,
                  text = "You did not save any recipes yet!",
                  modifier = Modifier.testTag("SavedScreenText"))
            }
      })
}
