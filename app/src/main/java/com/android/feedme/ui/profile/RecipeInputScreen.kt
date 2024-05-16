package com.android.feedme.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.R
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeStepViewModel
import com.android.feedme.ui.component.IngredientList
import com.android.feedme.ui.component.StepList
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.FindRecipeIcons

/**
 * A composable function that generates the recipe input screen.
 *
 * This function provides the UI interface of the recipe input page.
 *
 * @param navigationActions: NavigationActions object to handle navigation events
 * @param profileViewModel: ProfileViewModel object to interact with profile data
 */
@Composable
fun RecipeInputScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel
    // TODO: Integrate ViewModel with UI
) {
  val inputViewModel: InputViewModel = viewModel()
  val recipeStepViewModel: RecipeStepViewModel = viewModel()

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("RecipeInputScreen"),
      topBar = { TopBarNavigation(title = "Add Recipe", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(Route.PROFILE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      // Floating Button to create a recipe
      floatingActionButton = {
        FloatingActionButton(
            modifier = Modifier.testTag("ValidateRecipeButton"),
            containerColor = FindRecipeIcons,
            contentColor = Color.White,
            onClick = {
              // TODO : redirect to create recipe screen
            }) {
              Icon(imageVector = Icons.Default.Check, contentDescription = "Validate Recipe Icon")
            }
      },
      content = { padding -> RecipeBox(padding, inputViewModel, recipeStepViewModel) })
}

/**
 * Box containing all the recipe input fields.
 *
 * @param paddingValues: PaddingValues object to provide padding values
 * @param inputViewModel: InputViewModel object to interact with input data
 * @param recipeStepViewModel: RecipeStepViewModel object to interact with recipe step data
 */
@Composable
fun RecipeBox(
    paddingValues: PaddingValues,
    inputViewModel: InputViewModel,
    recipeStepViewModel: RecipeStepViewModel
) {
  Column(
      modifier = Modifier.padding(paddingValues).testTag("RecipeInputBox"),
      verticalArrangement = Arrangement.Top) {
        RecipeInputTopContent()
        IngredientList(inputViewModel, Modifier.heightIn(max = 150.dp))
        StepList(recipeStepViewModel = recipeStepViewModel)
      }
}

/** Box containing the recipe picture, title, and description input fields. */
@Composable
fun RecipeInputTopContent() {
  Column(
      modifier = Modifier.testTag("RecipeInputBox").heightIn(max = 150.dp),
      verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              RecipePicture()
              TitleBox()
            }
      }
}

/** Composable function that displays the recipe picture. */
@Composable
fun RecipePicture() {
  // TODO integrate with gallery after implementing viewmodel
  /*
    AsyncImage(
        model = profileViewModel._imageUrl.collectAsState().value,
  */
  Image(
      painter = painterResource(id = R.drawable.add_image),
      contentDescription = "Recipe Picture",
      modifier =
          Modifier.padding(20.dp)
              .width(70.dp)
              .height(70.dp)
              .clip(CircleShape)
              .clickable(onClick = { /* TODO with ViewModel */})
              .testTag("RecipePicture"))
}

/** Box containing the title and description input fields. */
@Composable
fun TitleBox() {
  val mod = Modifier.fillMaxWidth().height(56.dp)
  Column(
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp).height(120.dp)) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            singleLine = true,
            modifier = mod.testTag("RecipeTitleInput"),
            label = {
              Text(text = "Title", modifier = Modifier.background(color = Color.Transparent))
            })
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = mod.testTag("RecipeDescriptionInput"),
            label = {
              Text(
                  text = "Description (optional)",
                  modifier = Modifier.background(color = Color.Transparent))
            })
      }
}
