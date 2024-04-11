package com.android.feedme.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * Displays a bottom navigation bar with tabs based on provided destinations.
 *
 * This composable function creates a bottom navigation menu, marking the current selection and
 * handling tab selection changes. It dynamically generates navigation tabs from a list of top-level
 * destinations, providing a consistent navigation experience.
 *
 * @param selectedItem The ID of the currently active tab.
 * @param onTabSelect Callback for handling tab selection changes.
 * @param tabList List of top-level destinations for tab creation.
 */
@Composable
fun BottomNavigationMenu(
    selectedItem: String,
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
) {
  NavigationBar(modifier = Modifier.testTag("BottomNavigationMenu")) {
    tabList.forEach { replyDestination ->
      NavigationBarItem(
          selected = selectedItem == replyDestination.route,
          onClick = { onTabSelect(replyDestination) },
          icon = {
            Icon(imageVector = replyDestination.icon, contentDescription = replyDestination.textId)
          },
          label = { Text(replyDestination.textId) },
          modifier = Modifier.testTag(replyDestination.textId))
    }
  }
}
