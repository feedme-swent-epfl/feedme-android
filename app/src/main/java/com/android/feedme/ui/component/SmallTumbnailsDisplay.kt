package com.android.feedme.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.twotone.Bookmark
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Recipe
import com.android.feedme.ui.theme.YellowStar
import com.android.feedme.ui.theme.YellowStarBlackOutline

/**
 * Composable function to display a list of recipes as small thumbnails with additional information.
 * Each thumbnail includes an image, rating, cooking time, and title. Thumbnails are displayed two
 * by two in a column. If the image couldn't be downloaded from internet a message is displayed
 * instead.
 *
 * @param listRecipe List of Recipe objects to be displayed.
 */
@Composable
fun SmallThumbnailsDisplay(listRecipe: List<Recipe>) {
  // Calculate the width of each image based on the screen width
  val NB_IMAGE_PER_LINE = 2
  val IMAGE_WIDTH = LocalConfiguration.current.screenWidthDp / NB_IMAGE_PER_LINE
  val ImageSuccessfulDownload = remember { mutableStateOf(false) }

  LazyVerticalGrid(columns = GridCells.Adaptive(minSize = IMAGE_WIDTH.dp)) {
    items(listRecipe.size) { i ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(3.dp)) {
            // Recipe photo, downloaded from internet
            AsyncImage(
                model = listRecipe[i].imageUrl,
                contentDescription = "Recipe Image",
                modifier = Modifier.testTag("Recipe Image"),
                onSuccess = { ImageSuccessfulDownload.value = true })

            // Display a warning message if image couldn't be downloaded from internets
            if (!ImageSuccessfulDownload.value) {
              Text("Failed to download image", modifier = Modifier.testTag("Fail Image Download"))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

              // Star icon for ratings
              Box(contentAlignment = Alignment.Center) {
                // Larger black star to act as the outline
                Icon(
                    imageVector = Icons.TwoTone.Star,
                    contentDescription = "Star Outline",
                    tint = YellowStarBlackOutline,
                    modifier =
                        Modifier.size(26.dp) // Make this star slightly larger to show as the edge
                    )
                // Smaller yellow star to act as the inner part
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Star Icon",
                    tint = YellowStar,
                    modifier = Modifier.size(17.dp) // Smaller than the outline star
                    )
              }

              // Recipe rating
              Text(
                  String.format("%.1f", listRecipe[i].rating),
                  modifier = Modifier.padding(end = 10.dp))

              // Clock icon for the time
              // There is no clock icon in Material, so for now i'm using the "build" icon
              Icon(
                  imageVector = Icons.Outlined.Timer,
                  contentDescription = "Info Icon",
                  modifier = Modifier.size(26.dp).padding(end = 3.dp))

              // Recipe time
              Text(
                  text = "${listRecipe[i].time.toInt()} '",
                  modifier = Modifier.padding(end = 45.dp))

              // Save button, to keep the recipe accessible even offline
              // There is no save icon in Material, so for now i'm using the "build" icon
              IconButton(onClick = { /*TODO call to the database function for saving recipes*/}) {
                Icon(
                    imageVector = Icons.TwoTone.Bookmark,
                    contentDescription = "Save Icon",
                    modifier = Modifier.size(26.dp).padding(start = 4.dp))
              }
            }
            // Recipe Title
            Text(text = listRecipe[i].title)
          }
    }
  }
}

/*@Preview
@Composable
fun SmallThumbnailsDisplayPreview() {
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
          time = 75.0,
          rating = 4.5,
          userid = "PasDavid",
          difficulty = "Intermediate",
          "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

  Box(modifier = Modifier.background(Color.White)) {
      SmallThumbnailsDisplay(listRecipe = listOf(recipe1, recipe1, recipe1, recipe1, recipe1))
  }
}

 */
