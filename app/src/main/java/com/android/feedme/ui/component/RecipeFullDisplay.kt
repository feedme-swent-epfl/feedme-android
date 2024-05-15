package com.android.feedme.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.twotone.Bookmark
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.CommentViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.BlueUsername
import com.android.feedme.ui.theme.YellowStar
import com.android.feedme.ui.theme.YellowStarBlackOutline

/**
 * Displays a full recipe view. The screen contains the [TopBarNavigation], the
 * [BottomNavigationMenu] and the recipes display. The recipe display includes : an image, general
 * information's (time, userId of the creator and rating), list of ingredients and list of steps to
 * prepare the recipe.
 *
 * @param route The current route of the screen.
 * @param navigationActions Gives access to the navigation actions.
 * @param recipeViewModel The [RecipeViewModel] to get the recipe from.
 * @param profileViewModel The [ProfileViewModel] to get the profile of the recipe creator.
 */
@Composable
fun RecipeFullDisplay(
    route: String,
    navigationActions: NavigationActions,
    recipeViewModel: RecipeViewModel,
    profileViewModel: ProfileViewModel
) {
  val recipe = recipeViewModel.recipe.collectAsState().value

  // Fetch the profile of the user who created the recipe
  recipe?.let { profileViewModel.fetchProfile(it.userid) }
  val profile = profileViewModel.viewingUserProfile.collectAsState().value
  var showDialog by remember { mutableStateOf(false) }
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopBarNavigation(
            title = recipe?.title ?: "Not Found",
            navAction = navigationActions,
            rightIcon = Icons.TwoTone.Bookmark,
            rightIconOnClickAction = { null /* TODO() Save recipe offline*/ })
      },
      bottomBar = {
        BottomNavigationMenu(route, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { showDialog = true },
            content = { Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add") })
      },
      content = { padding ->
        if (recipe != null) {
          LazyColumn(modifier = Modifier.padding(padding)) {
            item { ImageDisplay(recipe) }
            item { GeneralInfoDisplay(recipe, profile, navigationActions, profileViewModel) }
            item { IngredientTitleDisplay() }
            items(recipe.ingredients) { ingredient -> IngredientDisplay(ingredient = ingredient) }
            item { IngredientStepsDividerDisplay() }
            items(recipe.steps) { step -> StepDisplay(step = step) }
          }
        }
          if (showDialog) {
              Dialog(onDismissRequest = { showDialog = false }) {
                  CreateComment(
                      profileViewModel = profileViewModel,
                      recipeViewModel = recipeViewModel,
                      commentViewModel = CommentViewModel()
                  )
              }
          }
        }
      )
}
/**
 * Displays the image associated with a recipe.
 *
 * @param recipe The [Recipe].
 * @param modifier The modifier for the image layout.
 */
@Composable
fun ImageDisplay(recipe: Recipe, modifier: Modifier = Modifier) {
  val imageSuccessfulDownload = remember { mutableStateOf(false) }
  AsyncImage(
      model = recipe.imageUrl,
      contentDescription = "Recipe Image",
      modifier = modifier.fillMaxWidth().testTag("Recipe Image"),
      onSuccess = { imageSuccessfulDownload.value = true })

  // Display a warning message if image couldn't be downloaded from internets
  if (!imageSuccessfulDownload.value) {
    Text("Failed to download image", modifier = Modifier.testTag("Fail Image Download"))
  }
}

/**
 * Displays general information about a recipe : time, userId of the creator and rating.
 *
 * @param recipe The [Recipe] to display information for.
 * @param profile The [Profile] of the recipe creator.
 * @param navigationActions The [NavigationActions] to navigate to other screens.
 * @param profileViewModel The [ProfileViewModel] to get the profile of the recipe creator.
 * @param modifier The [Modifier] for the layout of the row wrapping the content.
 */
@Composable
fun GeneralInfoDisplay(
    recipe: Recipe,
    profile: Profile?,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
  Row(
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically,
      modifier = modifier.fillMaxWidth().height(45.dp).testTag("General Infos Row")) {

        // Recipe creator's userId
        Spacer(modifier = Modifier.weight(1f))
        if (profile != null) {
          Text(
              text = "By user ",
              textAlign = TextAlign.Center,
              style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
          Text(
              modifier =
                  Modifier.clickable(
                      onClick = {
                        profileViewModel.setViewingProfile(profile)
                        navigationActions.navigateTo(Screen.PROFILE)
                      }),
              text = profile.username,
              textAlign = TextAlign.Center,
              color = BlueUsername,
              style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
        }
        Spacer(modifier = Modifier.weight(1f))

        // Recipe ratings
        Box(contentAlignment = Alignment.Center) {
          // Larger black star to act as the outline
          Icon(
              imageVector = Icons.TwoTone.Star,
              contentDescription = "Star Outline",
              tint = YellowStarBlackOutline,
              modifier = Modifier.size(26.dp) // Make this star slightly larger to show as the edge
              )
          // Smaller yellow star to act as the inner part
          Icon(
              imageVector = Icons.Rounded.Star,
              contentDescription = "Star Icon",
              tint = YellowStar,
              modifier = Modifier.size(17.dp) // Smaller than the outline star
              )
        }
        Text(
            text = recipe.rating.toString(),
            modifier = Modifier.padding(start = 4.dp).testTag("Text Rating"),
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
