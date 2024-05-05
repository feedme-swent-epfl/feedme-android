package com.android.feedme.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import com.android.feedme.model.data.Step

/**
 * This composable is used to display a list of steps in a recipe.
 * @param modifier Modifier to apply to this layout node.
 * @param stepViewModel ViewModel to manage the steps in the recipe.
 *
 */
@Composable
fun StepList(
    modifier: Modifier = Modifier,
) {
  val totalSteps = 4
  LazyColumn(modifier = modifier) {
    items(totalSteps) { index ->
      val step = Step(1, "woah", "woa")
      StepInput(
          step = step,
          onStepChanged = { newStep ->
            // stepViewModel.updateStep(index, newStep)
          },
          onDeleteStep = {
            // stepViewModel.deleteStep(index)
          })
    }
  }
}

/**
 * This composable is used to display a single step in a recipe.
 * @param step The step to display.
 * @param onStepChanged Callback to call when the step is changed.
 * @param onDeleteStep Callback to call when the step is deleted.
 */
@Composable
fun StepInput(step: Step, onStepChanged: (Step) -> Unit, onDeleteStep: () -> Unit) {
  var expanded by remember { mutableStateOf(false) }
  var title by remember { mutableStateOf(step.title) }
  var description by remember { mutableStateOf(step.description) }
  var titleError by remember { mutableStateOf(false) }
  var descriptionError by remember { mutableStateOf(false) }

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
              },
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
              onClick = onDeleteStep, modifier = Modifier.size(24.dp).testTag("StepInputDelete")) {
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
            onStepChanged(Step(step.stepNumber, description, title))
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
