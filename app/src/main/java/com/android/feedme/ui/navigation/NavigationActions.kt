package com.android.feedme.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigationActions(private val navController: NavHostController) {
  fun navigateTo(destination: TopLevelDestination) {
    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }
}

object Route {
  const val HOME = "Home"
  const val EXPLORE = "Explore"
  const val CREATE = "Create"
  const val PROFILE = "Profile"
  const val SETTINGS = "Settings"
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(route = Route.HOME, icon = Icons.Default.Home, textId = "Home"),
        TopLevelDestination(route = Route.EXPLORE, icon = Icons.Default.Search, textId = "Explore"),
        TopLevelDestination(route = Route.CREATE, icon = Icons.Default.Add, textId = "Create"),
        TopLevelDestination(
            route = Route.PROFILE, icon = Icons.Default.AccountCircle, textId = "Profile"),
        TopLevelDestination(
            route = Route.SETTINGS, icon = Icons.Default.Settings, textId = "Settings"))
