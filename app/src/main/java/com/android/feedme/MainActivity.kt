package com.android.feedme

import android.annotation.SuppressLint
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
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.resources.C
import com.android.feedme.ui.SavedScreen
import com.android.feedme.ui.auth.LoginScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.camera.GalleryScreen
import com.android.feedme.ui.component.RecipeFullDisplay
import com.android.feedme.ui.find.FindRecipeScreen
import com.android.feedme.ui.home.LandingPage
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.profile.EditProfileScreen
import com.android.feedme.ui.profile.FriendsScreen
import com.android.feedme.ui.profile.ProfileScreen
import com.android.feedme.ui.settings.SettingsScreen
import com.android.feedme.ui.theme.feedmeAppTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
  @SuppressLint("UnrememberedGetBackStackEntry")
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
              val inputViewModel: InputViewModel = viewModel<InputViewModel>()

              // Set up the nested navigation graph
              NavHost(navController = navController, startDestination = Route.AUTHENTICATION) {
                navigation(startDestination = Screen.AUTHENTICATION, route = Route.AUTHENTICATION) {
                  composable(Screen.AUTHENTICATION) { LoginScreen(navigationActions) }
                }

                navigation(startDestination = Screen.HOME, route = Route.HOME) {
                  composable(Screen.HOME) {
                    // Create a shared view model for Recipe
                    val recipeViewModel = viewModel<RecipeViewModel>()
                    LandingPage(navigationActions, recipeViewModel)
                  }
                  composable(Screen.RECIPE) {
                    // Link the shared view model to the composable
                    val navBackStackEntry = navController.getBackStackEntry(Screen.HOME)
                    val recipeViewModel = viewModel<RecipeViewModel>(navBackStackEntry)
                    RecipeFullDisplay(navigationActions, recipeViewModel)
                  }
                }

                navigation(startDestination = Screen.SAVED, route = Route.SAVED) {
                  composable(Screen.SAVED) { SavedScreen(navigationActions, Route.SAVED) }
                }

                navigation(startDestination = Screen.FIND_RECIPE, route = Route.FIND_RECIPE) {
                  composable(Screen.FIND_RECIPE) {
                    FindRecipeScreen(navigationActions, inputViewModel)
                  }
                  composable(Screen.CAMERA) { CameraScreen(navigationActions) }
                  composable(Screen.GALLERY) { GalleryScreen(navigationActions, 10) }
                }

                navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
                  composable(Screen.PROFILE) { ProfileScreen(navigationActions, profileViewModel) }
                  composable(Screen.EDIT_PROFILE) {
                    EditProfileScreen(navigationActions, profileViewModel)
                  }
                  composable(Screen.FRIENDS) { backStackEntry ->
                    backStackEntry.arguments?.getString("showFollowers")?.let {
                      FriendsScreen(navigationActions, profileViewModel, it.toInt())
                    }
                  }
                }
                navigation(startDestination = Screen.SETTINGS, route = Route.SETTINGS) {
                  composable(Screen.SETTINGS) { SettingsScreen(navigationActions) }
                }
              }
            }
      }
    }
  }
}
