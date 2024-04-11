package com.android.feedme.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.draw.clip
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
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.TopBarColor

/** Composable function that generates the landing page */
@Composable
fun LandingPage(navigationActions: NavigationActions) {
  /** For testing purposes, keeping arguments static for now TODO ViewModel for the recipe lists */
  val testRecipes: List<Recipe> =
      listOf(
          Recipe(
              recipeId = "lasagna1",
              title = "Tasty Lasagna",
              description = "a",
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
              userid = "PasDavid",
              difficulty = "Intermediate",
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"),
          Recipe(
              recipeId = "lasagna1",
              title = "Tasty Lasagna",
              description = "je decris ma recette",
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
              userid = "PasDavid",
              difficulty = "Intermediate",
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"),
          Recipe(
              recipeId = "lasagna1",
              title = "Tasty Lasagna",
              description = "a",
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
              userid = "PasDavid",
              difficulty = "Intermediate",
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"),
          Recipe(
              recipeId = "lasagna1",
              title = "Tasty Lasagna",
              description = "a",
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
              userid = "PasDavid",
              difficulty = "Intermediate",
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images"))
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("LandingScreen"),
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(Route.HOME, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding -> RecipeList(padding, testRecipes) })
}

/**
 * A function that iterates over the list of recipes and generates a card for each one
 *
 * @param padding : the PaddingValues to allow the function to be added in the scaffold
 * @param recipes : the list of recipes to be displayed
 */
@Composable
fun RecipeList(padding: PaddingValues, recipes: List<Recipe>) {
  LazyColumn(modifier = Modifier.padding(padding).testTag("RecipeList").background(TopBarColor)) {
    items(recipes) { recipe ->
      Card(
          modifier = Modifier.padding(16.dp).clickable(onClick = {}),
          elevation = CardDefaults.elevatedCardElevation()) {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()) {
                    Text(text = recipe.title, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    IconButton(
                        onClick = { /* Handle save icon click */},
                        modifier = Modifier.testTag("SaveIcon")) {
                          Icon(
                              imageVector = Icons.Outlined.Save, contentDescription = "Save Recipe")
                        }
                  }

              Row(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = "Recipe Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 100.dp, height = 100.dp).clip(CircleShape))
                Column(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                  Text(
                      text = recipe.description,
                      modifier = Modifier.fillMaxWidth(),
                      color = TemplateColor)
                  Text(
                      "@${recipe.userid}",
                      color = TemplateColor,
                      modifier =
                          Modifier.padding(top = 8.dp)
                              .clickable(
                                  onClick = { /* TODO : implement the clicking on username */})
                              .testTag("UserName"))
                }
              }
              Spacer(modifier = Modifier.height(8.dp).width(10.dp))
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    Column() {
                      Icon(
                          imageVector = Icons.Outlined.Timer,
                          contentDescription = null,
                          modifier = Modifier.size(24.dp).padding(end = 4.dp))
                      Text(
                          text = "${recipe.time.toInt()} min",
                          modifier = Modifier.padding(end = 8.dp))
                    }
                    OutlinedButton(
                        onClick = {},
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TemplateColor),
                        modifier = Modifier.weight(1f) // TODO() : fix button size
                        ) {
                          Text(recipe.difficulty)
                        }
                    OutlinedButton(
                        onClick = { /* TODO() implement the click on the rating that leads to comment section */},
                        border = BorderStroke(1.dp, Color.Transparent),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TemplateColor),
                        modifier =
                            Modifier.weight(1f)
                                .padding(start = 8.dp)
                                .width(
                                    4
                                        .dp) // TODO() : fix button size and align to the left of
                                             // the card
                                .testTag("Rating")) {
                          Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%.1f", recipe.rating),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(end = 4.dp))
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Rating",
                                modifier = Modifier.size(24.dp))
                          }
                        }
                  }
            }
          }
    }
  }
}
