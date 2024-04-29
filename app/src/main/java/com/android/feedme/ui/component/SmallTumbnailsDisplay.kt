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
  val imageWidth = LocalConfiguration.current.screenWidthDp / NB_IMAGE_PER_LINE
  val imageSuccessfulDownload = remember { mutableStateOf(false) }

  LazyVerticalGrid(columns = GridCells.Adaptive(minSize = imageWidth.dp)) {
    items(listRecipe.size) { i ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(3.dp)) {
            // Recipe photo, downloaded from internet
            AsyncImage(
                model = listRecipe[i].imageUrl,
                contentDescription = "Recipe Image",
                modifier = Modifier.testTag("Recipe Image"),
                onSuccess = { imageSuccessfulDownload.value = true })

            // Display a warning message if image couldn't be downloaded from internets
            if (!imageSuccessfulDownload.value) {
              Text("Failed to download image", modifier = Modifier.testTag("Fail Image Download"))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

              // Star icon for ratings
              Box(contentAlignment = Alignment.Center, modifier = Modifier.testTag("Star Icon")) {
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
                  modifier = Modifier.padding(end = 10.dp).testTag("Text Rating"))

              // Clock icon for the time
              // There is no clock icon in Material, so for now i'm using the "build" icon
              Icon(
                  imageVector = Icons.Outlined.Timer,
                  contentDescription = "Timer Icon",
                  modifier = Modifier.size(26.dp).padding(end = 3.dp))

              // Recipe time
              Text(
                  text = "${listRecipe[i].time.toInt()} '",
                  modifier = Modifier.padding(end = 45.dp).testTag("Text Time"))

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
            Text(text = listRecipe[i].title, modifier = Modifier.testTag("Text Title"))
          }
    }
  }
}
