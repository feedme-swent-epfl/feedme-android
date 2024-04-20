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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.resources.C
import com.android.feedme.ui.CreateScreen
import com.android.feedme.ui.NotImplementedScreen
import com.android.feedme.ui.auth.LoginScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.home.LandingPage
import com.android.feedme.ui.home.RecipeFullDisplay
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.profile.EditProfileScreen
import com.android.feedme.ui.profile.FriendsScreen
import com.android.feedme.ui.profile.ProfileScreen
import com.android.feedme.ui.profile.ProfileViewModel
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
              val profileViewModel: ProfileViewModel = viewModel<ProfileViewModel>()

              // Set up the nested navigation graph
              NavHost(navController = navController, startDestination = Route.AUTHENTICATION) {
                navigation(startDestination = Screen.AUTHENTICATION, route = Route.AUTHENTICATION) {
                  composable(Screen.AUTHENTICATION) { LoginScreen(navigationActions) }
                }

                navigation(startDestination = Screen.HOME, route = Route.HOME) {
                  composable(Screen.HOME) { LandingPage(navigationActions) }
                  composable(Screen.RECIPE) { backStackEntry ->
                    backStackEntry.arguments?.getString("recipeId")?.let {
                      RecipeFullDisplay(navigationActions)
                    }
                  }
                }

                navigation(startDestination = Screen.EXPLORE, route = Route.EXPLORE) {
                  composable(Screen.EXPLORE) {
                    NotImplementedScreen(navigationActions, Route.EXPLORE)
                  }
                }

                navigation(startDestination = Screen.CREATE, route = Route.CREATE) {
                  composable(Screen.CREATE) { CreateScreen(navigationActions) }
                  composable(Screen.CAMERA) { CameraScreen(navigationActions) }
                }

                navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
                  composable(Screen.PROFILE) { ProfileScreen(navigationActions, profileViewModel) }
                  composable(Screen.EDIT_PROFILE) {
                    EditProfileScreen(navigationActions, profileViewModel)
                  }
                  composable(Screen.FRIENDS) { backStackEntry ->
                    backStackEntry.arguments?.getString("showFollowers")?.let {
                      FriendsScreen(navigationActions, profileViewModel, mode = it.toInt())
                    }
                  }
                }
                navigation(startDestination = Screen.SETTINGS, route = Route.SETTINGS) {
                  composable(Screen.SETTINGS) {
                    NotImplementedScreen(navigationActions, Route.SETTINGS)
                  }
                }
              }
            }
      }
    }
  }
}
