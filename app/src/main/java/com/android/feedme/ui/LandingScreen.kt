package com.android.feedme.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TextBarColor

/**
 * Composable function that generates the landing page
 *
 * @param navigationActions The navigation actions instance for handling back navigation.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LandingPage(navigationActions: NavigationActions, recipeList: List<Recipe>) {
  Scaffold(
      modifier = Modifier
          .fillMaxSize()
          .testTag("LandingScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.HOME, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { RecipeList(recipeList) })
}

/**
 * A function that iterates over the list of recipes and generates a card for each one
 *
 * @param recipes : the list of recipes to be displayed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeList(recipes: List<Recipe>) {
  Column(modifier = Modifier
      .testTag("RecipeList")
      .padding(top = 60.dp)
      .background(TextBarColor)) {
      // Search bar + filters icon
      Row (
          modifier = Modifier
              .background(Color.White),
          verticalAlignment = Alignment.CenterVertically) {
          SearchBar(
              query = "",
              active = true,
              onActiveChange = {},
              onQueryChange = {},
              onSearch = {},
              modifier = Modifier.width(300.dp).padding(end = 10.dp).height(50.dp)
          ) {
              //Text("Search recipe or friend")
          }
          // The filters' icon
          Icon(
              imageVector = Icons.Outlined.FilterList,
              contentDescription = "Filter",
              modifier =
              Modifier
                  .size(48.dp)
                  .clickable(onClick = { })
                  .padding(8.dp)
                  .testTag("FilterClick"))
      }
      // Scrollable list of recipes
      LazyColumn(
          modifier = Modifier
              .testTag("RecipeList")
              .padding(top = 10.dp)
              .background(TextBarColor)
      ) {
          items(recipes) { recipe ->
              // Recipe card
              Card(
                  modifier = Modifier
                      .padding(16.dp)
                      .clickable(onClick = { /* TODO() */ }),
                  elevation = CardDefaults.elevatedCardElevation()
              ) {
                  Column(
                      modifier = Modifier
                          .fillMaxWidth()
                          .background(Color.White)
                          .padding(16.dp)
                  ) {
                      // Top layer of the card
                      Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween,
                          modifier = Modifier.fillMaxWidth()
                      ) {
                          Text(text = recipe.title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                          IconButton(
                              onClick = { /* Handle save icon click TODO() */ },
                              modifier = Modifier.testTag("SaveIcon")
                          ) {
                              Icon(
                                  imageVector = Icons.Outlined.Save,
                                  contentDescription = "Save Recipe"
                              )
                          }
                      }

                      // Middle layer of the card
                      Row(
                          modifier = Modifier
                              .height(100.dp)
                              .fillMaxWidth()
                      ) {
                          // Recipe image extracted from the URL
                          AsyncImage(
                              model = recipe.imageUrl,
                              contentDescription = "Recipe Image",
                              contentScale = ContentScale.Fit,
                              modifier = Modifier
                                  .size(width = 100.dp, height = 100.dp)
                                  .clip(CircleShape)
                          )
                          Column(
                              modifier = Modifier
                                  .height(100.dp)
                                  .fillMaxWidth()
                          ) {
                              Text(
                                  text = recipe.description,
                                  modifier = Modifier.fillMaxWidth(),
                                  color = TemplateColor
                              )
                              Text(
                                  "@${recipe.userid}",
                                  color = TemplateColor,
                                  modifier =
                                  Modifier
                                      .padding(top = 8.dp)
                                      .clickable(
                                          onClick = { /* TODO : implement the clicking on username */ })
                                      .testTag("UserName")
                              )
                          }
                      }
                      Spacer(
                          modifier = Modifier
                              .height(8.dp)
                              .width(10.dp)
                      )
                      // Bottom layer of the card
                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                          // Time
                          Column {
                              Icon(
                                  imageVector = Icons.Outlined.Timer,
                                  contentDescription = null,
                                  modifier = Modifier
                                      .size(24.dp)
                                      .padding(end = 4.dp)
                              )
                              Text(
                                  text = "${recipe.time.toInt()} min",
                                  modifier = Modifier.padding(end = 8.dp)
                              )
                          }
                          OutlinedButton(
                              onClick = { /* TODO() - I don't think we want this to be clickable (not a button ?) */ },
                              border = BorderStroke(1.dp, Color.Black),
                              colors = ButtonDefaults.outlinedButtonColors(contentColor = TemplateColor),
                              modifier = Modifier.weight(1f) // TODO() : fix button size
                          ) {
                              Text(recipe.difficulty)
                          }
                          OutlinedButton(
                              onClick = { /* TODO() implement the click on the rating that leads to comment section */ },
                              border = BorderStroke(1.dp, Color.Transparent),
                              colors = ButtonDefaults.outlinedButtonColors(contentColor = TemplateColor),
                              modifier =
                              Modifier
                                  .weight(1f)
                                  .padding(start = 8.dp)
                                  .width(4.dp) // TODO() : fix button size and align to the left of
                                  // the card
                                  .testTag("Rating")
                          ) {
                              // Rating icon and text
                              Row(verticalAlignment = Alignment.CenterVertically) {
                                  Text(
                                      text = String.format("%.1f", recipe.rating),
                                      fontWeight = FontWeight.Medium,
                                      modifier = Modifier.padding(end = 4.dp)
                                  )
                                  Icon(
                                      imageVector = Icons.Outlined.Star,
                                      contentDescription = "Rating",
                                      modifier = Modifier.size(24.dp)
                                  )
                              }
                          }
                      }
                  }
              }
          }
      }
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewLanding() {
    val testRecipes: List<Recipe> =
        listOf(
            Recipe(
                recipeId = "lasagna1",
                title = "Tasty Lasagna",
                description = "Description of the recipe",
                ingredients =
                listOf(
                    IngredientMetaData(
                        quantity = 2.0,
                        measure = MeasureUnit.ML,
                        ingredient = Ingredient("Tomato", "Vegetables", "tomatoID"))),
                steps = listOf(Step(1, "a", "Step1")),
                tags = listOf("Meat"),
                time = 1.15,
                rating = 4.5,
                userid = "username",
                difficulty = "Intermediate",
                "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"),
        )
    LandingPage(navigationActions = NavigationActions(rememberNavController()), testRecipes)
}
