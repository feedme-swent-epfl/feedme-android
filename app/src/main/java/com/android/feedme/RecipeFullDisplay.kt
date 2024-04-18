package com.android.feedme

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.twotone.Bookmark
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.BlueUser
import com.android.feedme.ui.theme.YellowStar

/**
 * Displays a full recipe view. The screen contains the [TopBarNavigation], the
 * [BottomNavigationMenu] and the recipes display. The recipe display includes : an image, general
 * information's (time, userId of the creator and rating), list of ingredients and list of steps to
 * prepare the recipe.
 *
 * @param recipe The [Recipe] to display.
 * @param modifier The modifier for the scaffold layout.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeFullDisplay(recipe: Recipe, modifier: Modifier = Modifier) {
  Scaffold(
      modifier = modifier.testTag("Scaffold"),
      topBar = {
        TopBarNavigation(
            title = recipe.title,
            navAction = null /* TODO() Go back to precedent screen*/,
            rightIcon = Icons.TwoTone.Bookmark,
            rightIconOnClickAction = { null /* TODO() Save recipe offline*/ })
      },
      bottomBar = {
        BottomNavigationMenu(selectedItem = "", onTabSelect = {}, tabList = TOP_LEVEL_DESTINATIONS)
      }) {
        LazyColumn(
            contentPadding = PaddingValues(0.dp), modifier = Modifier.testTag("LazyColumn")) {
              item { ImageDisplay(recipe = recipe) }
              item { GeneralInfosDisplay(recipe = recipe) }
              item { IngredientTitleDisplay() }
              items(recipe.ingredients) { ingredient -> IngredientDisplay(ingredient = ingredient) }
              item { IngredientStepsDividerDisplay() }
              items(recipe.steps) { step -> StepDisplay(step = step) }
            }
      }
}
/**
 * Displays the image associated with a recipe.
 *
 * @param recipe The [Recipe].
 * @param modifier The modifier for the image layout.
 */
@Composable
fun ImageDisplay(recipe: Recipe, modifier: Modifier = Modifier) {
  AsyncImage(
      model = recipe.imageUrl,
      contentDescription = "Recipe Image",
      modifier = modifier.fillMaxWidth().testTag("Recipe Image"))
}

/**
 * Displays general information about a recipe : time, userId of the creator and rating.
 *
 * @param recipe The [Recipe] to display information for.
 * @param modifier The [Modifier] for the layout of the row wrapping the content.
 */
@Composable
fun GeneralInfosDisplay(recipe: Recipe, modifier: Modifier = Modifier) {
  Row(
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier.fillMaxWidth().height(45.dp)) {

        // Recipe time
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Rounded.AccessTime,
            contentDescription = "Time Icon",
            modifier = Modifier.testTag("Time Icon"))
        Text(
            text = recipe.time.toString(),
            modifier = Modifier.padding(start = 4.dp),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))

        // Recipe creator's userId
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "By user ",
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
        Text(
            text = recipe.userid,
            textAlign = TextAlign.Center,
            color = BlueUser,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
        Spacer(modifier = Modifier.weight(1f))

        // Recipe rating
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Rating Icon",
            tint = YellowStar,
            modifier = Modifier.testTag("Rating Icon"))
        Text(
            text = recipe.rating.toString(),
            modifier = Modifier.padding(start = 4.dp),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
        Spacer(modifier = Modifier.weight(1f))
      }
  HorizontalDivider(thickness = 2.dp, modifier = Modifier.testTag("Horizontal Divider 1"))
}

/**
 * Displays the title for the ingredients section.
 *
 * @param modifier The [Modifier] for the text layout.
 */
@Composable
fun IngredientTitleDisplay(modifier: Modifier = Modifier) {
  Text(
      text = "Ingredients",
      style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
      modifier = modifier.padding(start = 16.dp, top = 8.dp).testTag("Ingredient Title"))
}

/**
 * Displays an ingredient with its quantity, measure, and name. Each ingredient is displayed as a
 * text with a bullet point at the beginning.
 *
 * @param ingredient The [IngredientMetaData] to display.
 * @param modifier The [Modifier] for the text layout.
 */
@Composable
fun IngredientDisplay(ingredient: IngredientMetaData, modifier: Modifier = Modifier) {
  val bullet = "\u2022" // Bullet point unicode character
  val ingredientText = "${ingredient.quantity} ${ingredient.measure} ${ingredient.ingredient.name}"
  Text(
      text =
          buildAnnotatedString {
            pushStyle(SpanStyle(fontWeight = FontWeight(1000)))
            append(bullet)
            append(" ")
            pop()
            append(ingredientText)
          },
      style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
      modifier = modifier.padding(top = 10.dp, start = 16.dp).testTag("Ingredient Description"))
}

/**
 * Displays a horizontal divider to separate ingredient and step sections.
 *
 * @param modifier The [Modifier] for the divider layout.
 */
@Composable
fun IngredientStepsDividerDisplay(modifier: Modifier = Modifier) {
  HorizontalDivider(
      thickness = 2.dp,
      modifier = modifier.padding(top = 8.dp, bottom = 8.dp).testTag("Horizontal Divider 2"))
}

/**
 * Displays a [Step] of a [Recipe], including its number, title, and description.
 *
 * @param step The [Step] to display.
 * @param modifier The [Modifier] for the column (containing description) layout.
 */
@Composable
fun StepDisplay(step: Step, modifier: Modifier = Modifier) {
  // Step title
  Text(
      "Step ${step.stepNumber}: ${step.title}",
      style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
      modifier = Modifier.padding(start = 16.dp, bottom = 8.dp).testTag("Step Title"))
  Column(modifier = modifier) {
    Text(
        text = step.description,
        style = MaterialTheme.typography.bodyMedium,
        modifier =
            modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp).testTag("Step Description"),
        textAlign = TextAlign.Justify)
  }
}
