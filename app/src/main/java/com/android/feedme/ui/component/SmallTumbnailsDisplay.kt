package com.android.feedme.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
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
fun SmallThumbnailsDisplay(
    listRecipe: List<Recipe>,
    navigationActions: NavigationActions,
    recipeViewModel: RecipeViewModel
) {
  // Calculate the width of each image based on the screen width, we want to display 2 images per
  // line
  val imageWidth = LocalConfiguration.current.screenWidthDp / 2

  // Calculate the height of the grid based on the number of recipes and the height of each card
  // 340 is the height of each card with padding
  val gridHeight = ((listRecipe.size / 2) + (listRecipe.size % 2)) * 340

  LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = imageWidth.dp),
      userScrollEnabled = false,
      modifier = Modifier.height(gridHeight.dp)) {
        items(listRecipe.size) { i ->
          // Fetch the profile of the user who created the recipe
          recipeViewModel.fetchProfile(listRecipe[i].userid)

          Card(
              modifier =
                  Modifier.padding(8.dp)
                      .clickable(
                          onClick = {
                            recipeViewModel.selectRecipe(listRecipe[i])
                            navigationActions.navigateTo("Recipe/${Route.PROFILE}")
                          })
                      .testTag("RecipeSmallCard"),
              elevation = CardDefaults.elevatedCardElevation()) {
                val imageSuccessfulDownload = remember { mutableStateOf(false) }
                // Recipe photo, downloaded from internet
                AsyncImage(
                    model = listRecipe[i].imageUrl,
                    contentDescription = "Recipe Image",
                    modifier = Modifier.testTag("Recipe Image"),
                    onSuccess = { imageSuccessfulDownload.value = true })
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.background(Color.White)) {
                      // Display a warning message if image couldn't be downloaded from internets
                      if (!imageSuccessfulDownload.value) {
                        Text(
                            "Failed to download image",
                            modifier = Modifier.testTag("Fail Image Download"))
                      }

                      Row(
                          modifier = Modifier.fillMaxWidth(),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.padding(start = 10.dp)) {
                              // Star icon for ratings
                              Box(
                                  contentAlignment = Alignment.Center,
                                  modifier = Modifier.testTag("Star Icon")) {
                                    // Larger black star to act as the outline
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = "Star Outline",
                                        tint = YellowStarBlackOutline,
                                        modifier =
                                            Modifier.size(
                                                26.dp) // Make this star slightly larger to show
                                        // as the
                                        // edge
                                        )
                                    // Smaller yellow star to act as the inner part
                                    Icon(
                                        imageVector = Icons.Sharp.Star,
                                        contentDescription = "Star Icon",
                                        tint = YellowStar,
                                        modifier =
                                            Modifier.size(17.dp) // Smaller than the outline star
                                        )
                                  }

                              // Recipe rating
                              Text(
                                  String.format("%.1f", listRecipe[i].rating),
                                  modifier = Modifier.padding(end = 10.dp).testTag("Text Rating"))
                            }

                            // Save button, to keep the recipe accessible even offline
                            // There is no save icon in Material, so for now i'm using the "build"
                            // icon
                            IconButton(
                                modifier = Modifier.padding(end = 10.dp),
                                onClick = { /*TODO call to the database function for saving recipes*/}) {
                                  Icon(
                                      imageVector = Icons.Outlined.BookmarkBorder,
                                      contentDescription = "Save Icon",
                                      modifier = Modifier.size(26.dp).padding(start = 4.dp))
                                }
                          }

                      // Recipe Title
                      Text(
                          text = listRecipe[i].title,
                          modifier = Modifier.padding(bottom = 10.dp).testTag("Text Title"))
                    }
              }
        }
      }
}
