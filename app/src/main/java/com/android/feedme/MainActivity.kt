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
import com.android.feedme.model.data.CommentRepository
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.AuthViewModel
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.model.viewmodel.GenerateViewModel
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.resources.C
import com.android.feedme.ui.auth.LoginScreen
import com.android.feedme.ui.auth.WelcomeScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.camera.DisplayPicture
import com.android.feedme.ui.component.RecipeFullDisplay
import com.android.feedme.ui.generate.FindRecipeScreen
import com.android.feedme.ui.generate.GeneratedRecipesScreen
import com.android.feedme.ui.home.LandingPage
import com.android.feedme.ui.home.SavedRecipesScreen
import com.android.feedme.ui.home.SearchScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.profile.EditProfileScreen
import com.android.feedme.ui.profile.FriendsScreen
import com.android.feedme.ui.profile.ProfileScreen
import com.android.feedme.ui.profile.RecipeInputScreen
import com.android.feedme.ui.settings.SettingsScreen
import com.android.feedme.ui.theme.FeedMeAppTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

  @SuppressLint("UnrememberedGetBackStackEntry")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize Firebase
    val firebase = FirebaseFirestore.getInstance()
    ProfileRepository.initialize(firebase)
    RecipeRepository.initialize(firebase)
    IngredientsRepository.initialize(firebase)
    CommentRepository.initialize(firebase)
    setContent {
      FeedMeAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              // ViewModels for the app
              val profileViewModel: ProfileViewModel = viewModel<ProfileViewModel>()
              val searchViewModel: SearchViewModel = viewModel<SearchViewModel>()
              val authViewModel: AuthViewModel = viewModel<AuthViewModel>()
              val homeViewModel: HomeViewModel = viewModel<HomeViewModel>()
              val cameraViewModel: CameraViewModel = viewModel<CameraViewModel>()
              val generateViewModel = viewModel<GenerateViewModel>()
              val inputViewModel = InputViewModel(this)

              // Navigation host for the app
              val navController = rememberNavController()
              val navigationActions = NavigationActions(navController, profileViewModel)

              // Set up the nested navigation graph
              NavHost(navController = navController, startDestination = Route.AUTHENTICATION) {
                navigation(startDestination = Screen.AUTHENTICATION, route = Route.AUTHENTICATION) {
                  composable(Screen.AUTHENTICATION) {
                    LoginScreen(navigationActions, authViewModel)
                  }
                  composable(Screen.WELCOME) {
                    WelcomeScreen(navigationActions, profileViewModel, authViewModel)
                  }
                  composable(Screen.EDIT_PROFILE) {
                    EditProfileScreen(navigationActions, profileViewModel)
                  }
                }

                navigation(startDestination = Screen.HOME, route = Route.HOME) {
                  composable(Screen.HOME) {
                    // Create a shared view model for Recipe
                    val recipeViewModel = viewModel<RecipeViewModel>()
                    LandingPage(
                        navigationActions,
                        recipeViewModel,
                        homeViewModel,
                        profileViewModel,
                        searchViewModel)
                  }
                  composable(Screen.SEARCH) {
                    val recipeViewModel = viewModel<RecipeViewModel>()
                    SearchScreen(
                        navigationActions,
                        searchViewModel,
                        recipeViewModel,
                        homeViewModel,
                        profileViewModel)
                  }
                }

                navigation(startDestination = Screen.SAVED, route = Route.SAVED) {
                  composable(Screen.SAVED) {
                    val recipeViewModel = viewModel<RecipeViewModel>()
                    SavedRecipesScreen(
                        navigationActions, profileViewModel, recipeViewModel, homeViewModel)
                  }
                }

                navigation(startDestination = Screen.FIND_RECIPE, route = Route.FIND_RECIPE) {
                  composable(Screen.FIND_RECIPE) {
                    FindRecipeScreen(
                        navigationActions, inputViewModel, profileViewModel, generateViewModel)
                  }
                  composable(Screen.CAMERA) {
                    CameraScreen(navigationActions, cameraViewModel, inputViewModel)
                  }
                  composable(Screen.ANALYZE_PICTURE) {
                    DisplayPicture(navigationActions, inputViewModel, cameraViewModel)
                  }
                  composable(Screen.GENERATE) {
                    val recipeViewModel = viewModel<RecipeViewModel>()
                    GeneratedRecipesScreen(
                        navigationActions, generateViewModel, recipeViewModel, profileViewModel)
                  }
                }

                navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
                  composable(Screen.PROFILE) {
                    val recipeViewModel = viewModel<RecipeViewModel>()
                    ProfileScreen(navigationActions, profileViewModel, recipeViewModel)
                  }
                  composable(Screen.EDIT_PROFILE) {
                    EditProfileScreen(navigationActions, profileViewModel)
                  }
                  composable(Screen.ADD_RECIPE) {
                    RecipeInputScreen(
                        navigationActions, profileViewModel, cameraViewModel, inputViewModel)
                  }
                  composable(Screen.FRIENDS) { backStackEntry ->
                    backStackEntry.arguments?.getString("showFollowers")?.let {
                      FriendsScreen(navigationActions, profileViewModel, it.toInt())
                    }
                  }
                }
                navigation(startDestination = Screen.SETTINGS, route = Route.SETTINGS) {
                  composable(Screen.SETTINGS) {
                    SettingsScreen(navigationActions, profileViewModel)
                  }
                }

                composable(Screen.RECIPE) { backStackEntry ->
                  backStackEntry.arguments?.getString("sourceRoute")?.let {
                    val backScreen =
                        when (it) {
                          Route.HOME ->
                              if (homeViewModel.isOnLanding.value) Screen.HOME else Screen.SEARCH
                          Route.SAVED -> Screen.SAVED
                          Route.PROFILE -> Screen.PROFILE
                          Route.FIND_RECIPE -> Screen.GENERATE
                          else -> {
                            ""
                          }
                        }
                    // Link the shared view model to the composable
                    val navBackStackEntry = navController.getBackStackEntry(backScreen)
                    val recipeViewModel = viewModel<RecipeViewModel>(navBackStackEntry)
                    RecipeFullDisplay(
                        it, navigationActions, recipeViewModel, profileViewModel, cameraViewModel)
                  }
                }
              }
            }
      }
    }
  }
}
