package com.android.feedme.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.ui.component.LoadMoreButton
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.profile.FriendsCard

/**
 * Composable function for the Search Screen. This function displays the search screen with the
 * search bar and the filtered content.
 *
 * @param navigationActions: the navigation actions to be performed
 * @param navigationActions: the navigation actions to be performed
 * @param searchViewModel: the [SearchViewModel] view model for the search functionality
 * @param recipeViewModel: the [RecipeViewModel] view model for the recipe functionality
 * @param profileViewModel: the [ProfileViewModel] view model for the profile functionality
 */
@Composable
fun SearchScreen(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    recipeViewModel: RecipeViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("SearchScreen"),
      topBar = { TopBarNavigation("Search Results", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(Route.HOME, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = {
        SearchScreenContent(
            it,
            navigationActions,
            searchViewModel,
            recipeViewModel,
            homeViewModel,
            profileViewModel)
      })
}

/**
 * Composable function for the Search Screen Content. This function displays the search screen
 * content with the option to switch between filtered recipes and accounts.
 *
 * @param padding: the [PaddingValues] for the content padding
 * @param navigationActions: the navigation actions to be performed
 * @param searchViewModel: the [SearchViewModel] view model for the search functionality
 * @param recipeViewModel: the [RecipeViewModel] view model for the recipe functionality
 * @param homeViewModel: the [HomeViewModel] view model for the home functionality
 * @param profileViewModel: the [ProfileViewModel] view model for the profile functionality
 */
@Composable
fun SearchScreenContent(
    padding: PaddingValues,
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    recipeViewModel: RecipeViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel
) {
  val tabSearchList = listOf("Recipes", "Accounts")
  var selectedTabIndex by remember { mutableIntStateOf(0) }

  val recipes = searchViewModel.filteredRecipes.collectAsState()
  val profiles = searchViewModel.filteredProfiles.collectAsState()

  homeViewModel.setOnLanding(false)

  Column(modifier = Modifier.padding(padding)) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("TabRow")) {
          tabSearchList.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                modifier = Modifier.testTag(if (index == 0) "TabRecipes" else "TabAccounts"))
          }
        }
    when (selectedTabIndex) {
      0 ->
          FilteredContent(
              recipes.value,
              emptyList(),
              navigationActions,
              selectedTabIndex,
              recipeViewModel,
              profileViewModel,
              searchViewModel,
              searchViewModel::loadMoreRecipes)
      1 ->
          FilteredContent(
              emptyList(),
              profiles.value,
              navigationActions,
              selectedTabIndex,
              recipeViewModel,
              profileViewModel,
              searchViewModel,
              searchViewModel::loadMoreProfiles)
    }
  }
}

/**
 * The filtered [List<Recipe>] or [List<Profile>] content to be displayed.
 *
 * @param recipes: the list of recipes to be displayed
 * @param profiles: the list of profiles to be displayed
 * @param navigationActions: the navigation actions to be performed
 * @param mode: the mode to determine if the content is recipes or profiles
 * @param recipeViewModel: the [RecipeViewModel] view model for the recipe functionality
 * @param profileViewModel: the [ProfileViewModel] view model for the profile functionality
 * @param searchViewModel: the [SearchViewModel] view model for the search functionality
 * @param loadMore: the function to load more recipes or profiles
 */
@Composable
fun FilteredContent(
    recipes: List<Recipe>,
    profiles: List<Profile>,
    navigationActions: NavigationActions,
    mode: Int,
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel,
    loadMore: () -> Unit
) {
  val moreRecipes = searchViewModel.lastRecipe.collectAsState()
  val moreProfiles = searchViewModel.lastProfile.collectAsState()

  if (recipes.isEmpty() && mode == 0 || profiles.isEmpty() && mode == 1) {
    Column(
        modifier = Modifier.fillMaxSize().testTag("EmptyList"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          when (mode) {
            0 ->
                Text(
                    text = "No recipes were found",
                    modifier = Modifier.padding(16.dp).testTag("NoRecipes"))
            1 ->
                Text(
                    text = "No accounts were found",
                    modifier = Modifier.padding(16.dp).testTag("NoAccounts"))
          }
        }
  } else {
    LazyColumn(modifier = Modifier.fillMaxSize().testTag("FilteredList")) {
      when (mode) {
        0 -> {
          items(recipes) { recipe ->
            // Fetch the profile of the user who created the recipe
            LaunchedEffect(recipe.userid) { recipeViewModel.fetchProfile(recipe.userid) }
            val recipesProfiles by recipeViewModel.profiles.collectAsState()
            val profile = recipesProfiles[recipe.userid]

            RecipeCard(
                Route.HOME, recipe, profile, navigationActions, recipeViewModel, profileViewModel)
          }

          // Display Load more button if there are more recipes to fetch
          if (moreRecipes.value != null) {
            item { LoadMoreButton(loadMore) }
          }
        }
        1 -> {
          items(profiles) { profile -> FriendsCard(profile, navigationActions, profileViewModel) }

          // Display Load more button if there are more profiles to fetch
          if (moreProfiles.value != null) {
            item { LoadMoreButton(loadMore) }
          }
        }
      }
    }
  }
}
