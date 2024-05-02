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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
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

@Preview (showBackground = true)
@Composable
fun RecipeSmallCardPreview() {
    val recipe =
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
                    ingredient = Ingredient("Tomato", "Vegetables", "tomatoID")
                )
            ),
            steps =
            listOf(
                Step(
                    1,
                    "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                            "\n" +
                            "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
                    "Make the Meat Sauce")
            ),
            tags = listOf("Meat"),
            time = 45.0,
            rating = 4.5,
            userid = "username",
            difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")
  RecipeSmallCard(recipe)

}
