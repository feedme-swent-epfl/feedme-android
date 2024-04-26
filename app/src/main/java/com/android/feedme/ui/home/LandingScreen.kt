package com.android.feedme.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.twotone.Bookmark
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
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.component.SearchBarFun
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TextBarColor
import com.android.feedme.ui.theme.YellowStar

/**
 * Composable function that generates the landing page / landing screen
 *
 * @param navigationActions The [NavigationActions] instance for handling back navigation.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LandingPage(
    navigationActions: NavigationActions,
    recipeViewModel: RecipeViewModel = RecipeViewModel()
) {
  /* Note that this val is temporary for this sprint, we're awaiting the implementation of the
   * ViewModels to properly do this part. */
  val testRecipes: List<Recipe> =
      listOf(
          Recipe(
              recipeId = "lasagna1",
              title = "Tasty Lasagna",
              description =
                  "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
              ingredients =
                  listOf(
                      IngredientMetaData(
                          quantity = 2.0,
                          measure = MeasureUnit.ML,
                          ingredient = Ingredient("Tomato", "Vegetables", "tomatoID"))),
              steps =
                  listOf(
                      Step(
                          1,
                          "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                              "\n" +
                              "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
                          "Make the Meat Sauce")),
              tags = listOf("Meat"),
              time = 45.0,
              rating = 4.5,
              userid = "username",
              difficulty = "Intermediate",
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"),
      )
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("LandingScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.HOME, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { RecipeDisplay(navigationActions, testRecipes, recipeViewModel) })
}

/**
 * A function that iterates over the list of recipes and generates a card for each one
 *
 * @param recipes : the list of [Recipe] to be displayed
 */
@Composable
fun RecipeDisplay(
    navigationActions: NavigationActions,
    recipes: List<Recipe>,
    recipeViewModel: RecipeViewModel
) {

  Column(
      modifier = Modifier.testTag("CompleteScreen").padding(top = 60.dp).background(Color.White)) {

        // Search bar + filters icon
        SearchBarFun()

        // Scrollable list of recipes
        LazyColumn(
            modifier =
                Modifier.testTag("RecipeList").padding(top = 10.dp).background(TextBarColor)) {
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
                                  navigationActions.navigateTo(Screen.RECIPE)
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
                                      Modifier.padding(end = 20.dp)
                                          .clickable { /* TODO () : access the comments */}
                                          .testTag("Rating")) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = "Rating",
                                        tint = YellowStar,
                                        modifier = Modifier.size(30.dp).padding(end = 6.dp))
                                    Text(text = String.format("%.1f", recipe.rating))
                                  }
                              // Cooking time
                              Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Timer,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp).padding(end = 4.dp))
                                Text(
                                    text = "${recipe.time.toInt()} '",
                                    modifier = Modifier.padding(end = 8.dp),
                                )
                              }
                              // Share icon
                              IconButton(
                                  onClick = { /* TODO() adding the options to share */},
                                  modifier = Modifier.testTag("ShareIcon")) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Icon on Recipe Card",
                                        modifier = Modifier.size(24.dp).padding(4.dp))
                                  }
                              Spacer(modifier = Modifier.weight(1f))
                              // Save icon
                              IconButton(
                                  onClick = { /* TODO() add saving logic here */},
                                  modifier = Modifier.testTag("SaveIcon")) {
                                    Icon(
                                        imageVector = Icons.TwoTone.Bookmark,
                                        contentDescription = "Bookmark Icon on Recipe Card",
                                        modifier = Modifier.size(24.dp).padding(start = 4.dp))
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
