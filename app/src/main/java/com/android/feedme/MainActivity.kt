package com.android.feedme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.resources.C
import com.android.feedme.ui.CreateScreen
import com.android.feedme.ui.LandingPage
import com.android.feedme.ui.NotImplementedScreen
import com.android.feedme.ui.auth.LoginScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.profile.FollowersScreen
import com.android.feedme.ui.profile.ProfileScreen
import com.android.feedme.ui.theme.feedmeAppTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize Firebase
    val firebase = FirebaseFirestore.getInstance()
    ProfileRepository.initialize(firebase)
    RecipeRepository.initialize(firebase)
    setContent {
      feedmeAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              // Navigation host for the app
              val navController = rememberNavController()
              val navigationActions = NavigationActions(navController)

              // Set up the navigation graph
              NavHost(navController = navController, startDestination = Route.PROFILE) {
                composable(Route.AUTHENTICATION) {
                  LoginScreen(navigationActions = navigationActions)
                }
                composable(Route.HOME) { LandingPage(navigationActions) }
                composable(Route.EXPLORE) { NotImplementedScreen(navigationActions, Route.EXPLORE) }
                composable(Route.CREATE) { CreateScreen(navigationActions) }
                composable(Route.PROFILE) { FollowersScreen(navigationActions) }
                composable(Route.SETTINGS) { ProfileScreen(navigationActions = navigationActions) }
                composable(Route.CAMERA) { CameraScreen(navigationActions) }
              }
            }
      }
    }
  }
}
