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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Recipe
import com.android.feedme.ui.theme.YellowStar

/**
 * Composable function to display a list of recipes as small thumbnails with additional information.
 * Each thumbnail includes an image, rating, cooking time, and title. Thumbnails are displayed two
 * by two in a column.
 *
 * @param listRecipe List of Recipe objects to be displayed.
 */
@Composable
fun SmallThumbnailsDisplay(listRecipe: List<Recipe>) {
  val IMAGE_WIDTH = LocalConfiguration.current.screenWidthDp / 2

  LazyVerticalGrid(columns = GridCells.Adaptive(minSize = IMAGE_WIDTH.dp)) {
    items(listRecipe.size) { i ->
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.testTag("Column").padding(3.dp)) {
            AsyncImage(
                model = listRecipe[i].imageUrl,
                contentDescription = "Recipe Image",
                modifier = Modifier.testTag("Recipe Image"))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                  imageVector = Icons.TwoTone.Star,
                  contentDescription = null,
                  tint = YellowStar,
                  modifier = Modifier.testTag("Star Icon").padding(end = 3.dp))

              Text(
                  listRecipe[i].rating.toString(),
                  modifier = Modifier.testTag("Rating").padding(end = 10.dp))

              // There is no clock icon in Material, so for now i'm using the "build" icon
              Icon(
                  imageVector = Icons.Outlined.Info,
                  contentDescription = null,
                  modifier = Modifier.testTag("Info Icon").padding(end = 3.dp))

              Text(
                  listRecipe[i].time.toString(),
                  modifier = Modifier.testTag("Time").padding(end = 45.dp))

              // There is no save icon in Material, so for now i'm using the "build" icon
              IconButton(
                  onClick = { /*TODO call to the database function for saving recipes*/},
                  modifier = Modifier.testTag("Save Icon")) {
                    Icon(imageVector = Icons.Outlined.Build, contentDescription = null)
                  }
            }

            Text(text = listRecipe[i].title, modifier = Modifier.testTag("Recipe Title"))

            Text(listRecipe[i].time.toString(), modifier = Modifier.padding(end = 45.dp))

            // There is no save icon in Material, so for now i'm using the "build" icon
            Icon(imageVector = Icons.Outlined.Build, contentDescription = null)
          }

      Text(text = listRecipe[i].title)
    }
  }
}
