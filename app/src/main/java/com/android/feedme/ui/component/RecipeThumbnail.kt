package com.android.feedme.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.twotone.Bookmark
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.theme.CardBackground

/**
 * A card that displays a recipe.
 *
 * @param recipe The recipe to display.
 */
@Composable
fun RecipeSmallCard(recipe: Recipe, recipeViewModel: RecipeViewModel = RecipeViewModel()) {

  Card(
      modifier =
          Modifier.padding(4.dp)
              .background(CardBackground.containerColor, MaterialTheme.shapes.medium),
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().height(250.dp).testTag("RecipeSmallCard"),
        horizontalAlignment = Alignment.CenterHorizontally) {
          ImageDisplay(
              recipe = recipe,
              Modifier.size(150.dp)
                  .align(Alignment.CenterHorizontally)
                  .clip(MaterialTheme.shapes.large)
                  .padding(2.dp),
          )

          Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceEvenly) {
                Row {
                  Icon(
                      imageVector = Icons.TwoTone.Star,
                      contentDescription = "Time Icon",
                      modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
                  Text("${recipe.rating}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.size(8.dp))
                Row {
                  Icon(
                      imageVector = Icons.Outlined.Timer,
                      contentDescription = "Time Icon",
                      modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
                  Text("${recipe.time}", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.size(8.dp))
                IconButton(onClick = { /* Handle save action here */}) {
                  Icon(
                      imageVector = Icons.TwoTone.Bookmark,
                      contentDescription = "Bookmark Icon",
                      modifier = Modifier.size(24.dp).padding(horizontal = 4.dp))
                }
              }
          Text(recipe.title, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.size(12.dp))
        }
  }
}
