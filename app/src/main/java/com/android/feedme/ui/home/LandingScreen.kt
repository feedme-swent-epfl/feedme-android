package com.android.feedme.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.component.SearchBarFun
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TextBarColor
import com.android.feedme.ui.theme.YellowStar
import com.android.feedme.ui.theme.YellowStarBlackOutline

/**
 * Composable function that generates the landing page / landing screen
 *
 * @param navigationActions The [NavigationActions] instance for handling back navigation.
 * @param recipeViewModel The [RecipeViewModel] instance of the recipe ViewModel.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun LandingPage(
    navigationActions: NavigationActions,
    recipeViewModel: RecipeViewModel = RecipeViewModel(),
    homeViewModel: HomeViewModel = HomeViewModel()
) {

  val recipes = homeViewModel.recipes.value

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("LandingScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.HOME, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { RecipeDisplay(it, navigationActions, recipes, homeViewModel, recipeViewModel) })
}

/**
 * A function that iterates over the list of recipes and generates a card for each one
 *
 * @param paddingValues : the padding values for the screen
 * @param navigationActions : the navigation actions for the screen
 * @param recipes : the list of [Recipe] to be displayed
 * @param homeViewModel : the [HomeViewModel] instance
 * @param recipeViewModel : the [RecipeViewModel] instance
 */
@Composable
fun RecipeDisplay(
    paddingValues: PaddingValues,
    navigationActions: NavigationActions,
    recipes: List<Recipe>,
    homeViewModel: HomeViewModel,
    recipeViewModel: RecipeViewModel
) {

  Column(
      modifier =
          Modifier.testTag("CompleteScreen").padding(paddingValues).background(Color.White)) {

        // Search bar + filters icon
        SearchBarFun(homeViewModel)

        // Scrollable list of recipes
        LazyColumn(
            modifier =
                Modifier.testTag("RecipeList").padding(top = 8.dp).background(TextBarColor)) {
              items(recipes) { recipe ->
                // Recipe card
                Card(
                    modifier =
                        Modifier.padding(16.dp)
                            .clickable(
                                onClick = {
                                  // Set the selected recipe in the view model and navigate to the
                                  // recipe screen
                                  recipeViewModel.selectRecipe(recipe)
                                  navigationActions.navigateTo("Recipe/${Route.HOME}")
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
                                  .padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                            // Time, rating, share and saving icon
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
                                              imageVector = Icons.TwoTone.Star,
                                              contentDescription = "Rating Outline",
                                              tint = YellowStarBlackOutline,
                                              modifier = Modifier.size(34.dp))
                                          // Smaller yellow star to act as the inner part
                                          Icon(
                                              imageVector = Icons.Rounded.Star,
                                              contentDescription = "Rating",
                                              tint = YellowStar,
                                              modifier = Modifier.size(23.dp))
                                        }
                                    Text(
                                        text = String.format("%.1f", recipe.rating),
                                        fontWeight = FontWeight.Bold,
                                    )
                                  }

                              Spacer(modifier = Modifier.width(10.dp))

                              // Cooking time
                              Icon(
                                  imageVector = Icons.Outlined.Timer,
                                  contentDescription = null,
                                  modifier = Modifier.size(34.dp).padding(end = 4.dp))
                              Text(
                                  text = "${recipe.time.toInt()} '",
                                  fontWeight = FontWeight.Bold,
                              )
                              Spacer(modifier = Modifier.width(3.dp))

                              // Share icon
                              IconButton(
                                  onClick = { /* TODO() adding the options to share */},
                                  modifier = Modifier.testTag("ShareIcon")) {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = "Share Icon on Recipe Card",
                                        modifier = Modifier.size(32.dp))
                                  }

                              Spacer(modifier = Modifier.weight(1f))
                              // Save icon
                              IconButton(
                                  onClick = { /* TODO() add saving logic here */},
                                  modifier = Modifier.testTag("SaveIcon")) {
                                    Icon(
                                        imageVector = Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Bookmark Icon on Recipe Card",
                                        modifier = Modifier.size(34.dp).padding(start = 4.dp))
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
                              Spacer(modifier = Modifier.weight(1f))
                              OutlinedButton(
                                  onClick = {},
                                  border = BorderStroke(1.dp, TemplateColor),
                                  colors =
                                      ButtonDefaults.outlinedButtonColors(
                                          contentColor = TemplateColor),
                              ) {
                                Text(recipe.difficulty)
                              }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            // Description of the recipe
                            Text(
                                text = recipe.description,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                color = TemplateColor)
                            Text(
                                "@${recipe.userid}",
                                color = TemplateColor,
                                modifier =
                                    Modifier.padding(bottom = 10.dp)
                                        .clickable(
                                            onClick = { /* TODO : implement the clicking on username */})
                                        .testTag("UserName"))
                          }
                    }
              }
            }
      }
}
