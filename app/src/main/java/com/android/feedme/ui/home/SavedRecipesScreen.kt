package com.android.feedme.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * Composable that displays the saved recipes screen. If the user has saved recipes, it displays the
 * list of recipes in a scrollable list. If the user has not saved any recipes, it displays a
 * message saying that there are no recipes saved yet.
 *
 * @param navigationActions Provides navigation actions for handling user interactions with the
 * @param profileViewModel The view model that provides the profile data.
 * @param searchViewModel The view model that provides the search data.
 * @param recipeViewModel The view model that provides the recipe data.
 */
@Composable
fun SavedRecipesScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel,
    recipeViewModel: RecipeViewModel
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("SavedScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.SAVED, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        val savedRecipes =
            profileViewModel.currentUserSavedRecipes.collectAsState(initial = listOf()).value
        if (savedRecipes.isEmpty()) {
          EmptySavedScreen(padding)
        } else {
          var recipes = listOf<Recipe>()
          for (recipe in savedRecipes) {
            recipeViewModel.fetchRecipe(recipe)
            recipes += recipeViewModel.recipe.collectAsState().value ?: Recipe()
          }
          RecipeDisplay(
              padding,
              navigationActions,
              recipes,
              searchViewModel,
              recipeViewModel,
              profileViewModel)
        }
      })
}

/**
 * Composable helper function that displays a list of saved recipes.
 *
 * @param padding The padding of the parent composable.
 */
@Composable
fun EmptySavedScreen(padding: PaddingValues) {
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
}
