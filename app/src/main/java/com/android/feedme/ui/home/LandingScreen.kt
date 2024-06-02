package com.android.feedme.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.ui.component.LoadMoreButton
import com.android.feedme.ui.component.SearchBarFun
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.BlueUsername
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TextBarColor
import com.android.feedme.ui.theme.YellowStar
import com.android.feedme.ui.theme.YellowStarBlackOutline

/**
 * Composable function that generates the landing page / landing screen
 *
 * @param navigationActions The [NavigationActions] instance for handling back navigation.
 * @param recipeViewModel The [RecipeViewModel] instance of the recipe ViewModel.
 * @param homeViewModel The [HomeViewModel] instance of the home ViewModel.
 * @param profileViewModel The [ProfileViewModel] instance of the profile ViewModel.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LandingPage(
    navigationActions: NavigationActions,
    recipeViewModel: RecipeViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("LandingScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.HOME, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = {
        homeViewModel.setOnLanding(true)
        RecipeDisplay(
            it,
            navigationActions,
            homeViewModel,
            searchViewModel,
            recipeViewModel,
            profileViewModel)
      })
}

/**
 * A function that iterates over the list of recipes and generates a card for each one
 *
 * @param paddingValues : the padding values for the screen
 * @param navigationActions : the navigation actions for the screen
 * @param homeViewModel : the [HomeViewModel] instance
 * @param searchViewModel : the [SearchViewModel] instance
 * @param recipeViewModel : the [RecipeViewModel] instance
 * @param profileViewModel : the [ProfileViewModel] instance
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RecipeDisplay(
    paddingValues: PaddingValues,
    navigationActions: NavigationActions,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel
) {
  val recipes = homeViewModel.recipes.collectAsState()

  Column(
      modifier =
          Modifier.testTag("CompleteScreen").padding(paddingValues).background(Color.White)) {

        // Search bar + filters icon
        SearchBarFun(Route.HOME, navigationActions, searchViewModel)

        // Scrollable list of recipes
        LazyColumn(
            modifier =
                Modifier.testTag("RecipeList").padding(top = 8.dp).background(TextBarColor)) {
              items(recipes.value) { recipe ->
                // Fetch the profile of the user who created the recipe
                LaunchedEffect(recipe.userid) { recipeViewModel.fetchProfile(recipe.userid) }
                val profiles by recipeViewModel.profiles.collectAsState()
                val profile = profiles[recipe.userid]

                // Recipe card
                RecipeCard(
                    Route.HOME,
                    recipe,
                    profile,
                    navigationActions,
                    recipeViewModel,
                    profileViewModel)
              }

              item { LoadMoreButton(homeViewModel::loadMoreRecipes) }
            }
      }
}

/**
 * Composable function for the Recipe Card. This function displays the recipe in a card format.
 *
 * @param recipe The [Recipe] to be displayed.
 * @param profile The [Profile] of the user who created the recipe.
 * @param navigationActions The [NavigationActions] instance for handling back navigation.
 * @param recipeViewModel The [RecipeViewModel] instance of the recipe ViewModel.
 * @param profileViewModel The [ProfileViewModel] instance of the profile ViewModel.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RecipeCard(
    route: String,
    recipe: Recipe,
    profile: Profile?,
    navigationActions: NavigationActions,
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel
) {
  Card(
      modifier =
          Modifier.padding(16.dp)
              .clickable(
                  onClick = {
                    // Set the selected recipe in the view model and navigate to the
                    // recipe screen
                    recipeViewModel.selectRecipe(recipe)
                    navigationActions.navigateTo("Recipe/${route}")
                  })
              .testTag("RecipeCard"),
      elevation = CardDefaults.elevatedCardElevation()) {
        AsyncImage(
            model = recipe.imageUrl,
            contentDescription = "Recipe Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(200.dp).fillMaxWidth())
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
              // Rating and saving icon
              Row(
                  modifier = Modifier.padding(4.dp).fillMaxWidth(),
                  horizontalArrangement = Arrangement.Absolute.Left,
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.clickable { /* TODO () : access the comments */}
                            .testTag("Rating")) {
                      // Star icon for ratings
                      Box(
                          contentAlignment = Alignment.Center,
                          modifier = Modifier.padding(end = 2.dp)) {
                            // Larger black star to act as the outline
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Rating Outline",
                                tint = YellowStarBlackOutline,
                                modifier = Modifier.size(35.dp))
                            // Smaller yellow star to act as the inner part
                            Icon(
                                imageVector = Icons.Sharp.Star,
                                contentDescription = "Rating",
                                tint = YellowStar,
                                modifier = Modifier.size(23.dp))
                          }
                      Text(
                          text = String.format("%.1f", recipe.rating),
                          fontWeight = FontWeight.Bold,
                      )
                    }

                Spacer(modifier = Modifier.width(15.dp))

                Spacer(modifier = Modifier.weight(1f))
                // Save icon
                val isSaved = remember { mutableStateOf(false) }

                // LaunchedEffect to trigger the Firestore check when the composable is first
                // composed
                LaunchedEffect(recipe) {
                  profileViewModel.savedRecipeExists(recipe.recipeId) { exists ->
                    isSaved.value = exists
                  }
                }

                IconButton(
                    onClick = {
                      if (isSaved.value) {
                        profileViewModel.removeSavedRecipes(recipe.recipeId)
                        isSaved.value = false // Update: now it reflects the change correctly
                      } else {
                        profileViewModel.addSavedRecipes(recipe.recipeId)
                        isSaved.value = true // Update: now it reflects the change correctly
                      }
                    },
                    modifier = Modifier.testTag("SaveIcon")) {
                      Icon(
                          imageVector =
                              if (isSaved.value) {
                                Icons.Rounded.Bookmark
                              } else {
                                Icons.Outlined.BookmarkBorder
                              },
                          contentDescription = "Bookmark Icon on Recipe Card",
                          modifier = Modifier.size(34.dp).padding(start = 4.dp),
                          tint =
                              if (isSaved.value) {
                                TemplateColor
                              } else {
                                YellowStarBlackOutline
                              })
                    }
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                // Title of the Recipe
                Text(
                    text = recipe.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = TemplateColor,
                    modifier = Modifier.padding(bottom = 10.dp, end = 10.dp))
              }
              if (profile != null) {
                Text(
                    modifier =
                        Modifier.padding(bottom = 10.dp)
                            .clickable(
                                onClick = {
                                  profileViewModel.setViewingProfile(profile)
                                  navigationActions.navigateTo(Screen.PROFILE)
                                })
                            .testTag("UserName"),
                    text = "@${profile.username}",
                    color = BlueUsername,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
              }

              Spacer(modifier = Modifier.height(10.dp))
              // Description of the recipe
              Text(
                  text = recipe.description,
                  modifier = Modifier.fillMaxWidth().height(50.dp),
                  color = TemplateColor)
            }
      }
}
