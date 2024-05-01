package com.android.feedme.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.ui.theme.InValidInput
import com.android.feedme.ui.theme.NoInput
import com.android.feedme.ui.theme.ValidInput

/**
 * Composable function for displaying a list of ingredients.
 *
 * @param modifier the modifier for this composable.
 * @param list the list of [IngredientMetaData] items to display. Default is null.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun IngredientList(
    inputViewModel: InputViewModel = InputViewModel(),
    modifier: Modifier = Modifier,
) {

  val totalIngredients by inputViewModel.totalIngredients.collectAsState()
  LazyColumn(modifier = modifier.testTag("LazyList")) {
    this.items(totalIngredients) { index ->
      val movableContent = movableContentOf {
        IngredientInput(inputViewModel.listOfIngredients.value[index]) { before, now, newIngredient
          ->
          inputViewModel.setUpdate(index, before, now, newIngredient)
        }
      }
      movableContent()
    }
  }
}

/**
 * Composable function for displaying an input field for ingredient details.
 *
 * @param ingredient the [IngredientMetaData] to display in the input fields.
 * @param action the action to perform on input changes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientInput(
    ingredient: IngredientMetaData? = null,
    action: (IngredientInputState?, IngredientInputState?, IngredientMetaData?) -> Unit
) {
  var name by remember { mutableStateOf(ingredient?.ingredient?.name ?: " ") }
  var quantity by remember { mutableDoubleStateOf(ingredient?.quantity ?: 0.0) }
  var dose by remember { mutableStateOf(ingredient?.measure ?: MeasureUnit.EMPTY) }

  val isComplete by remember {
    mutableStateOf(name.isNotBlank() && dose != MeasureUnit.EMPTY && quantity != 0.0)
  }

  var state by remember {
    mutableStateOf(
        if (isComplete) IngredientInputState.COMPLETE
        else if (name.isNotBlank() || dose != MeasureUnit.EMPTY || quantity != 0.0)
            IngredientInputState.SEMI_COMPLETE
        else IngredientInputState.EMPTY)
  }

  var isDropdownVisible by remember { mutableStateOf(false) }
  val suggestionIngredients =
      listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5") // Your list of items

  Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).height(70.dp),
      verticalAlignment = Alignment.CenterVertically) {

        // Ingredients Box
        Box(modifier = Modifier.weight(1.5f).height(60.dp).testTag("IngredientsBox")) {
          OutlinedTextField(
              colors = colorOfInputBoxes(state),
              value = name,
              isError = name == " " && state != IngredientInputState.EMPTY,
              onValueChange = {
                name = it
                isDropdownVisible = true
              },
              singleLine = true,
              modifier = Modifier.padding(end = 0.dp).testTag("IngredientsInput"),
              placeholder = { Text(text = "...") },
              label = {
                Text(text = "Ingredient", modifier = Modifier.background(color = Color.Transparent))
              })

          DropdownMenu(
              modifier = Modifier.height(120.dp),
              expanded = isDropdownVisible && name.isNotEmpty(),
              onDismissRequest = { isDropdownVisible = false },
              properties =
                  PopupProperties(
                      focusable = false, dismissOnClickOutside = false, dismissOnBackPress = false),
          ) {
            suggestionIngredients.forEach { item ->
              DropdownMenuItem(
                  modifier = Modifier.testTag("IngredientOption"),
                  text = { Text(text = item) },
                  onClick = {
                    name = item
                    isDropdownVisible = false
                    val beforeState = state
                    if (name != " ") {
                      state =
                          if (isComplete) IngredientInputState.COMPLETE
                          else IngredientInputState.SEMI_COMPLETE
                      action(
                          beforeState,
                          state,
                          IngredientMetaData(quantity, dose, Ingredient(name, "", "")))
                    }
                  })
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Quantity
        OutlinedTextField(
            colors = colorOfInputBoxes(state),
            isError = quantity == 0.0 && state != IngredientInputState.EMPTY,
            value = if (quantity == 0.0) " " else quantity.toString(),
            onValueChange = { // Check if the input is a valid number
              if (it.isNotEmpty() && it.toDoubleOrNull() != null && it.toDouble() >= 0.0) {
                quantity = it.toDouble()
                if (quantity != 0.0) {
                  action(state, state, IngredientMetaData(quantity, dose, Ingredient(name, "", "")))
                }
              }
            },
            singleLine = true,
            modifier = Modifier.weight(1f).height(60.dp).testTag("QuantityInput"),
            placeholder = { Text(text = "...") },
            label = {
              Text(text = "Quantity", modifier = Modifier.background(color = Color.Transparent))
            })

        Spacer(modifier = Modifier.width(8.dp))

        // Dose
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f).height(60.dp).testTag("DoseBox"),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
              OutlinedTextField(
                  colors = colorOfInputBoxes(state),
                  isError = dose == MeasureUnit.EMPTY && state != IngredientInputState.EMPTY,
                  readOnly = true,
                  value = if (dose != MeasureUnit.EMPTY) dose.toString() else " ",
                  onValueChange = {},
                  label = { Text("Dose") },
                  modifier = Modifier.menuAnchor().testTag("DoseInput"))
              ExposedDropdownMenu(
                  modifier = Modifier.height(120.dp),
                  expanded = expanded,
                  onDismissRequest = { expanded = false }) {
                    MeasureUnit.values().forEach { selectionOption ->
                      DropdownMenuItem(
                          text = { Text(text = selectionOption.toString()) },
                          onClick = {
                            dose = selectionOption
                            expanded = false
                            if (dose != MeasureUnit.EMPTY) {
                              val beforeState = state
                              state =
                                  if (isComplete) IngredientInputState.COMPLETE
                                  else IngredientInputState.SEMI_COMPLETE
                              action(
                                  beforeState,
                                  state,
                                  IngredientMetaData(quantity, dose, Ingredient(name, "", "")))
                            }
                          })
                    }
                  }
            }

        // Delete button for removing the ingredient
        if (state == IngredientInputState.SEMI_COMPLETE || state == IngredientInputState.COMPLETE) {
          IconButton(
              modifier = Modifier.testTag("DeleteIconButton"),
              onClick = {
                action(
                    state,
                    IngredientInputState.EMPTY,
                    IngredientMetaData(quantity, dose, Ingredient(name, "", "")))
              }) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).height(55.dp))
              }
        }
      }
}

/**
 * Function to determine the colors of input boxes based on input state.
 *
 * @param state the state of the input.
 * @return [TextFieldColors] object representing the colors of input boxes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun colorOfInputBoxes(state: IngredientInputState): TextFieldColors {
  return ExposedDropdownMenuDefaults.textFieldColors(
      unfocusedContainerColor = if (state != IngredientInputState.EMPTY) ValidInput else NoInput,
      focusedContainerColor = if (state != IngredientInputState.EMPTY) ValidInput else NoInput,
      errorContainerColor = InValidInput)
}

/** Enum class representing the state of an ingredient input. */
enum class IngredientInputState {
  EMPTY,
  SEMI_COMPLETE,
  COMPLETE
}
