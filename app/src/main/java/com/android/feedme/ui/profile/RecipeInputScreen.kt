package com.android.feedme.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.R
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeStepViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.component.IngredientList
import com.android.feedme.ui.component.StepList
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.FabColor
import com.android.feedme.ui.theme.TextBarColor

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
    profileViewModel: ProfileViewModel,
    recipeStepViewModel: RecipeStepViewModel,
    inputViewModel: InputViewModel,
    recipeViewModel: RecipeViewModel
    // TODO: Integrate ViewModel with UI
) {
  val title = remember { mutableStateOf("") }
  val description = remember { mutableStateOf("") }
  val error by recipeViewModel.errorMessageVisible.collectAsState()
  val snackbarHostStateError = remember { SnackbarHostState() }

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
            containerColor = FabColor,
            contentColor = TextBarColor,
            onClick = {
              // We validate the fields of our Recipe object before uploading it
              if (recipeViewModel.validateRecipe(
                  title.value,
                  description.value,
                  inputViewModel.listOfIngredientMetadatas.value,
                  recipeStepViewModel.steps.value,
                  profileViewModel.currentUserId!!,
                  // TODO : integrate it with photo picker
                  "")) {
                recipeViewModel.setRecipe(recipeViewModel.recipe.value!!)
                // TODO : we could display the recipe instead of going back to the profile
                navigationActions.navigateTo(Route.PROFILE)
              }
            }) {
              Icon(imageVector = Icons.Default.Check, contentDescription = "Validate Recipe Icon")
            }
      },
      content = { padding ->
        RecipeBox(padding, inputViewModel, recipeStepViewModel, title, description)
        // Box containing our snack bar host to be displayed when the recipe is not correctly filled
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.padding(padding).fillMaxSize()) {
              SnackbarHost(
                  hostState = snackbarHostStateError,
                  snackbar = { snackbarData ->
                    Snackbar(
                        modifier = Modifier.testTag("Error Snack Bar"),
                        snackbarData = snackbarData,
                        containerColor = Color.Red.copy(alpha = 0.5f),
                        contentColor = Color.White)
                  })
            }
        // We display the error when the recipe is not correctly filled
        LaunchedEffect(Unit) {
          recipeViewModel.errorMessageVisible.collect {
            if (error)
                snackbarHostStateError.showSnackbar(
                    message = "Error : Recipe not correctly filled in",
                    duration = SnackbarDuration.Short)
          }
        }
      })
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
    recipeStepViewModel: RecipeStepViewModel,
    title: MutableState<String>,
    description: MutableState<String>
) {
  Column(
      modifier = Modifier.padding(paddingValues).testTag("RecipeInputBox"),
      verticalArrangement = Arrangement.Top) {
        RecipeInputTopContent(title, description)
        IngredientList(inputViewModel, Modifier.heightIn(max = 150.dp))
        StepList(recipeStepViewModel = recipeStepViewModel)
      }
}

/** Box containing the recipe picture, title, and description input fields. */
@Composable
fun RecipeInputTopContent(title: MutableState<String>, description: MutableState<String>) {
  Column(
      modifier = Modifier.heightIn(max = 150.dp),
      verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              RecipePicture()
              TitleBox(title, description)
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
fun TitleBox(titleState: MutableState<String>, descriptionState: MutableState<String>) {
  val mod = Modifier.fillMaxWidth().height(56.dp)
  Column(
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp).height(150.dp)) {
        OutlinedTextField(
            value = titleState.value,
            onValueChange = { titleState.value = it },
            singleLine = true,
            modifier = mod.testTag("RecipeTitleInput"),
            textStyle = TextStyle(fontSize = 14.sp),
            label = {
              Text(text = "Title", modifier = Modifier.background(color = Color.Transparent))
            })
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = descriptionState.value,
            onValueChange = { descriptionState.value = it },
            modifier = mod.testTag("RecipeDescriptionInput"),
            textStyle = TextStyle(fontSize = 14.sp),
            label = {
              Text(
                  text = "Description (optional)",
                  modifier = Modifier.background(color = Color.Transparent))
            })
      }
}
