package com.android.feedme.ui.generate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.feedme.model.viewmodel.GenerateViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.home.RecipeCard
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * Composable function for the generated recipes Screen. This function displays the generated
 * recipes screen
 *
 * @param navigationActions: the navigation actions to be performed
 * @param generateViewModel: the [GenerateViewModel] view model for the generate functionality
 * @param recipeViewModel: the [RecipeViewModel] view model for the recipe functionality
 * @param profileViewModel: the [ProfileViewModel] view model for the profile functionality
 */
@Composable
fun GeneratedRecipesScreen(
    navigationActions: NavigationActions,
    generateViewModel: GenerateViewModel,
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("GeneratedRecipesScreen"),
      topBar = { TopBarNavigation("Generated Results", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            Route.FIND_RECIPE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = {
        GeneratedRecipesContent(
            it, navigationActions, generateViewModel, recipeViewModel, profileViewModel)
      })
}

/**
 * Composable function for the content. This function displays the list of recipes as [RecipeCard]
 *
 * @param padding: the [PaddingValues] for the content padding
 * @param navigationActions: the navigation actions to be performed
 * @param generateViewModel: the [GenerateViewModel] view model for the generate functionality
 * @param recipeViewModel: the [RecipeViewModel] view model for the recipe functionality
 * @param profileViewModel: the [ProfileViewModel] view model for the profile functionality
 */
@Composable
fun GeneratedRecipesContent(
    padding: PaddingValues,
    navigationActions: NavigationActions,
    generateViewModel: GenerateViewModel,
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel
) {
  val recipes = generateViewModel.generatedRecipes.collectAsState()

  if (recipes.value.isEmpty()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).testTag("EmptyList"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Text(
              text = "No recipes were found",
              modifier = Modifier.padding(16.dp).testTag("NoRecipes"))
        }
  } else {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).testTag("GeneratedList")) {
      items(recipes.value) { recipe ->
        LaunchedEffect(recipe.userid) { recipeViewModel.fetchProfile(recipe.userid) }
        val profiles by recipeViewModel.profiles.collectAsState()
        val profile = profiles[recipe.userid]
        RecipeCard(
            Route.FIND_RECIPE,
            recipe,
            profile,
            navigationActions,
            recipeViewModel,
            profileViewModel)
      }
    }
  }
}
