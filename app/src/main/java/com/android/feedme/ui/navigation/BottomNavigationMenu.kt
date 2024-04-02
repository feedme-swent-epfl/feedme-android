package com.android.feedme.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationMenu(
    selectedItem: String,
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
) {
  NavigationBar {
    tabList.forEach { replyDestination ->
      NavigationBarItem(
          selected = selectedItem == replyDestination.route,
          onClick = { onTabSelect(replyDestination) },
          icon = {
            Icon(imageVector = replyDestination.icon, contentDescription = replyDestination.textId)
          },
          label = { Text(replyDestination.textId) })
    }
  }
}
