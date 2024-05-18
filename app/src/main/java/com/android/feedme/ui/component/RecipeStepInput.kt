package com.android.feedme.ui.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.RecipeStepViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

/**
 * This composable is used to display a list of steps in a recipe.
 *
 * @param modifier Modifier to apply to this layout node.
 * @param recipeStepViewModel ViewModel to manage the steps in the recipe.
 */
@Preview
@Composable
fun StepList(
    modifier: Modifier = Modifier,
    recipeStepViewModel: RecipeStepViewModel = viewModel()
) {

  val steps by recipeStepViewModel.steps.collectAsState()

  Column(modifier = Modifier.fillMaxWidth().testTag("StepList")) {

    // Title for the list of steps with an icon to add a new step at the top
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp).testTag("StepListTitle")) {
          Text("Steps", style = MaterialTheme.typography.titleMedium)
          Spacer(modifier = Modifier.weight(1f))
          IconButton(
              onClick = { recipeStepViewModel.addStep(Step(steps.size + 1, "", "")) },
              modifier = Modifier.testTag("AddStepButton")) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add step")
              }
        }
    LazyColumn(modifier = modifier) {
      // Important to unsure the UI updates when the list of steps changes
      // Surprisingly hard to understand why this is needed but without it, does not update
      // correctly
      itemsIndexed(steps, key = { _, step -> step.hashCode() }) { index, step ->
        StepInput(
            step = step,
            onStepChanged = { newStep ->
              recipeStepViewModel.updateStep(index, newStep)
              Log.d("StepList", "Step changed: $newStep")
            },
            onDeleteStep = {
              recipeStepViewModel.deleteStep(step) // Pass index instead of step number or object
              Log.d("StepList", "Step deleted: $it")
            })
      }
    }
  }
}

/**
 * This composable is used to display a single step in a recipe.
 *
 * @param step The step to display.
 * @param onStepChanged Callback to call when the step is changed.
 * @param onDeleteStep Callback to call when the step is deleted.
 */
@Composable
fun StepInput(step: Step, onStepChanged: (Step) -> Unit, onDeleteStep: (Step) -> Unit) {
  var title by remember { mutableStateOf(step.title) }
  var description by remember { mutableStateOf(step.description) }
  var titleError by remember { mutableStateOf(step.title.isBlank()) }
  var descriptionError by remember { mutableStateOf(step.description.isBlank()) }
  var expanded by remember { mutableStateOf(descriptionError) }

  val titleState = remember { MutableStateFlow(title) }
  val descriptionState = remember { MutableStateFlow(description) }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(titleState) {
    titleState.debounce(500).collect { newTitle ->
      onStepChanged(Step(step.stepNumber, description, newTitle))
    }
  }

  LaunchedEffect(descriptionState) {
    descriptionState.debounce(500).collect { newDescription ->
      onStepChanged(Step(step.stepNumber, newDescription, title))
    }
  }

  Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).testTag("stepInput")) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(8.dp)) {
          Text(text = "${step.stepNumber}.", modifier = Modifier.align(Alignment.CenterVertically))
          Spacer(modifier = Modifier.width(8.dp)) // Provide spacing between text and text field
          OutlinedTextField(
              value = title,
              onValueChange = {
                title = it
                titleError = it.isBlank() // Validate title input
                titleState.value = it
              },
              singleLine = true,
              isError = titleError,
              label = { Text("Title") },
              modifier =
                  Modifier.weight(1f)
                      .testTag("StepInputTitle") // Give the text field flexible space
              )
          IconButton(
              onClick = { expanded = !expanded },
              modifier = Modifier.size(24.dp).testTag("StepInputExpand")) {
                Icon(
                    imageVector =
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand")
              }
          IconButton(
              onClick = { onDeleteStep(step) },
              modifier = Modifier.size(24.dp).testTag("StepInputDelete")) {
                Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Delete step")
              }
        }
    // Show error messages
    if (titleError) {
      Text(
          "Title cannot be empty.",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = 36.dp).testTag("StepInputTitleError"))
    }

    if (expanded) {
      OutlinedTextField(
          value = description,
          onValueChange = {
            description = it
            descriptionError = it.isBlank() // Validate description input
            descriptionState.value = it
          },
          isError = descriptionError,
          label = { Text("Description") },
          modifier =
              Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("StepInputDescription"))
    }

    if (expanded && descriptionError) {
      Text(
          "Description cannot be empty.",
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(start = 36.dp).testTag("StepInputDescriptionError"))
    }
  }
}
