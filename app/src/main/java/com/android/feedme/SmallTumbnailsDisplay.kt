package com.android.feedme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.ui.theme.YellowStar


/**
 * Composable function to display a list of recipes as small thumbnails with additional information.
 * Each thumbnail includes an image, rating, cooking time, and title.
 *
 * @param listRecipe List of Recipe objects to be displayed.
 */
@Composable
fun SmallThumbnailsDisplay(listRecipe: List<Recipe>) {
  val IMAGE_WIDTH = LocalConfiguration.current.screenWidthDp / 2

  LazyVerticalGrid(columns = GridCells.Adaptive(minSize = IMAGE_WIDTH.dp)) {
    items(listRecipe.size) { i ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(3.dp)) {
            AsyncImage(model = listRecipe[i].imageUrl, contentDescription = "Recipe Image")

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  imageVector = Icons.TwoTone.Star,
                  contentDescription = null,
                  tint = YellowStar,
                  modifier = Modifier.padding(end = 3.dp))

              Text(listRecipe[i].rating.toString(), modifier = Modifier.padding(end = 10.dp))

              // There is no clock icon in Material, so for now i'm using the "build" icon
              Icon(
                  imageVector = Icons.Outlined.Info,
                  contentDescription = null,
                  modifier = Modifier.padding(end = 3.dp))

              Text(listRecipe[i].time.toString(), modifier = Modifier.padding(end = 45.dp))

              // There is no save icon in Material, so for now i'm using the "build" icon
              Icon(imageVector = Icons.Outlined.Build, contentDescription = null)
            }

            Text(text = listRecipe[i].title)
          }
    }
  }
}

@Preview
@Composable
fun PreviewSmallThumbnailsDisplay() {
  // fake data because we don't have a database yet
  val recipe1 =
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
          "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")
  val testRecipes: List<Recipe> =
      listOf(
          recipe1,
          recipe1,
          recipe1,
          recipe1,
          recipe1,
      )
  SmallThumbnailsDisplay(listRecipe = testRecipes)
}
