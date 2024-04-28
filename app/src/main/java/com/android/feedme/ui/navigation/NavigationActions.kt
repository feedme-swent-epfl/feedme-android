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

/**
 * Handles navigation within an app's composable navigation graph.
 *
 * Utilizes a `NavHostController` to perform navigation actions, offering methods to navigate to
 * various app destinations with enhanced features like state preservation and avoiding duplicate
 * destinations in the stack.
 *
 * @param navController The controller managing app navigation.
 */
class NavigationActions(private val navController: NavHostController) {

  /**
   * Navigates to the specified [TopLevelDestination] destination.
   *
   * @param destination The top level destination to navigate to.
   */
  fun navigateTo(destination: TopLevelDestination) {
    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }

  /**
   * Navigates to the specified route.
   *
   * @param subRoute The route to navigate to.
   */
  fun navigateTo(subRoute: String) {
    // When calling this method, the back bottom would be available to go back using goBack()
    navController.navigate(subRoute)
  }

  /** Navigates back to the previous destination in the navigation stack. */
  fun goBack() {
    navController.popBackStack()
  }

  /**
   * Checks if there is a previous destination in the navigation stack that the app can navigate
   * back to.
   *
   * @return True if there is a previous destination to navigate back to, false otherwise.
   */
  fun canGoBack(): Boolean {
    return navController.previousBackStackEntry != null
  }
}

/** Contains route constants used for navigating within the app's top destinations */
object Route {
  const val AUTHENTICATION = "Authentication"
  const val HOME = "Home"
  const val EXPLORE = "Explore"
  const val CREATE = "Create"
  const val PROFILE = "Profile"
  const val SETTINGS = "Settings"
}

/** Contains sub-route constants used for navigating within the app's screens */
object Screen {
  const val AUTHENTICATION = "Authentication Screen"
  const val HOME = "Home Screen"
  const val EXPLORE = "Explore Screen"
  const val CREATE = "Create Screen"
  const val PROFILE = "Profile/{profileId}"
  const val SETTINGS = "Settings Screen"
  const val CAMERA = "Camera"
  const val EDIT_PROFILE = "Edit Profile"
  const val FRIENDS = "Friends/{showFollowers}"
  const val RECIPE = "Recipe"
}

/**
 * Represents a top-level destination within the app navigation.
 *
 * @property route The route associated with the destination.
 * @property icon The icon associated with the destination.
 * @property textId The resource ID of the text associated with the destination.
 */
data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

/** List of top-level destinations within the app navigation. */
val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(route = Route.HOME, icon = Icons.Default.Home, textId = "Home"),
        TopLevelDestination(route = Route.EXPLORE, icon = Icons.Default.Search, textId = "Explore"),
        TopLevelDestination(route = Route.CREATE, icon = Icons.Default.Add, textId = "Create"),
        TopLevelDestination(
            route = Route.PROFILE, icon = Icons.Default.AccountCircle, textId = "Profile"),
        TopLevelDestination(
            route = Route.SETTINGS, icon = Icons.Default.Settings, textId = "Settings"))
