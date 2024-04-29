package com.android.feedme.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.feedme.ui.theme.BottomIconColor
import com.android.feedme.ui.theme.TemplateColor

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
  NavigationBar(
      modifier = Modifier.fillMaxWidth().height(60.dp).testTag("BottomNavigationMenu"),
      // contentColor = TemplateColor,
      containerColor = TemplateColor) {
        tabList.forEach { replyDestination ->
          NavigationBarItem(
              selected = selectedItem == replyDestination.route,
              onClick = { onTabSelect(replyDestination) },
              icon = {
                Icon(
                    imageVector = replyDestination.icon,
                    contentDescription = replyDestination.textId,
                    tint = BottomIconColor,
                    modifier = Modifier.size(30.dp))
              },
              label = null,
              modifier = Modifier.testTag(replyDestination.textId))
        }
      }
}
