package com.android.feedme.ui.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material.icons.twotone.Bookmark
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.ui.theme.InValidInput
import com.android.feedme.ui.theme.NoInput
import com.android.feedme.ui.theme.ValidInput

private val ingredientsRepository = IngredientsRepository.instance

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
  val wasSaved by inputViewModel.wasSaved.collectAsState()
  val totalIngredients by inputViewModel.totalIngredientEntriesDisplayed.collectAsState()
  LazyColumn(modifier = modifier.testTag("LazyList")) {
    this.items(totalIngredients) { index ->
      val movableContent = movableContentOf {
        IngredientInput(inputViewModel.listOfIngredientMetadatas.value[index]) {
            before,
            now,
            newIngredient ->
          inputViewModel.updateListElementBehaviour(index, before, now, newIngredient)
        }
      }
      movableContent()
    }
    // Add a row at the end with three icons
    item {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          horizontalArrangement = Arrangement.Start) {
            IconButton(
                modifier = Modifier.testTag("SaveButton"),
                onClick = { inputViewModel.saveInFridge() }) {
                  Icon(
                      if (wasSaved) Icons.Filled.Bookmark else Icons.TwoTone.Bookmark,
                      contentDescription = "Saved")
                }
            IconButton(
                modifier = Modifier.testTag("ReloadButton"),
                onClick = { inputViewModel.loadFridge() }) {
                  Icon(Icons.Filled.Refresh, contentDescription = "Reload")
                }
            IconButton(
                modifier = Modifier.testTag("TrashButton"),
                onClick = { inputViewModel.resetList() }) {
                  Icon(Icons.Filled.Delete, contentDescription = "Trash")
                }
          }
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

  var ingredientCurrent by remember {
    mutableStateOf(ingredient?.ingredient ?: Ingredient(" ", "NO_ID", false, false))
  }

  var name by remember { mutableStateOf(ingredient?.ingredient?.name ?: " ") }
  var quantity by remember { mutableDoubleStateOf(ingredient?.quantity ?: 0.0) }
  var dose by remember { mutableStateOf(ingredient?.measure ?: MeasureUnit.EMPTY) }

  val isComplete by remember {
    derivedStateOf {
      name.isNotBlank() &&
          dose != MeasureUnit.EMPTY &&
          quantity != 0.0 &&
          (ingredientCurrent.id != "NO_ID") &&
          (ingredientCurrent.id != "")
    }
  }

  var isChecked by remember { mutableStateOf(isComplete) }

  var state by remember {
    mutableStateOf(
        if (isComplete) IngredientInputState.COMPLETE
        else if (name.isNotBlank() || dose != MeasureUnit.EMPTY || quantity != 0.0)
            IngredientInputState.SEMI_COMPLETE
        else IngredientInputState.EMPTY)
  }

  var isDropdownVisible by remember { mutableStateOf(false) }

  var filteredIngredients by remember { mutableStateOf(emptyList<Ingredient>()) }

  LaunchedEffect(name) {
    if (name != " ") {
      ingredientsRepository.getFilteredIngredients(
          name,
          { filteredIngredients = it },
          {
            Log.e(
                "IngredientList ",
                "Error Filtered Ingredients: Failed to retrieve Ingredient because ",
                it)
          })
    }
  }

  if (isChecked) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(start = 8.dp, top = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Column {
            Text(
                text = name,
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontStyle = FontStyle.Italic,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp).testTag("IngredientName"))
            Text(
                text = "$quantity $dose",
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic, fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier.testTag("Quantity&Dose"))
          }
          Row(
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    modifier = Modifier.testTag("ModifyIconButton"),
                    onClick = { isChecked = false }) {
                      Icon(
                          imageVector = Icons.Outlined.ModeEdit,
                          contentDescription = null,
                          modifier = Modifier.size(28.dp))
                    }

                DeleteButton(state, quantity, dose, name, action)
              }
        }
  } else {
    // Column for the input fields
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          // First row for the ingredient name
          Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            // Ingredients Box
            Box(modifier = Modifier.weight(1.5f).height(60.dp).testTag("IngredientsBox")) {
              OutlinedTextField(
                  colors = colorOfInputBoxes(state),
                  value = name,
                  isError =
                      (name == " " ||
                          ingredientCurrent.id == "NO_ID" ||
                          ingredientCurrent.id == "") && state != IngredientInputState.EMPTY,
                  onValueChange = {
                    name = it
                    isDropdownVisible = true
                  },
                  singleLine = true,
                  modifier =
                      Modifier.fillMaxWidth().padding(end = 0.dp).testTag("IngredientsInput"),
                  placeholder = { Text(text = "...") },
                  label = {
                    Text(
                        text = "Ingredient",
                        modifier = Modifier.background(color = Color.Transparent))
                  })

              DropdownMenu(
                  modifier = Modifier.height(120.dp),
                  expanded = isDropdownVisible && name.isNotEmpty(),
                  onDismissRequest = {
                    isDropdownVisible = false
                    val beforeState = state
                    if (name != " ") {
                      ingredientCurrent =
                          if (filteredIngredients.isNotEmpty()) {
                            filteredIngredients[0]
                          } else {
                            Ingredient(name, "NO_ID", false, false)
                          }
                      state =
                          if (isComplete) IngredientInputState.COMPLETE
                          else IngredientInputState.SEMI_COMPLETE
                      action(
                          beforeState, state, IngredientMetaData(quantity, dose, ingredientCurrent))
                    }
                  },
                  properties =
                      PopupProperties(
                          focusable = false,
                          dismissOnClickOutside = true,
                          dismissOnBackPress = true),
              ) {
                filteredIngredients.forEach { item ->
                  DropdownMenuItem(
                      modifier = Modifier.testTag("IngredientOption"),
                      text = { Text(text = item.name) },
                      onClick = {
                        name = item.name
                        isDropdownVisible = false
                        val beforeState = state
                        if (name != " ") {
                          ingredientCurrent = item
                          state =
                              if (isComplete) IngredientInputState.COMPLETE
                              else IngredientInputState.SEMI_COMPLETE
                          action(
                              beforeState,
                              state,
                              IngredientMetaData(quantity, dose, ingredientCurrent))
                        }
                      })
                }
                DropdownMenuItem(
                    modifier = Modifier.background(Color.LightGray).testTag("AddOption"),
                    text = { Text(text = "Add Ingredient") },
                    onClick = {
                      // TODO check validty of addition with Chatgbt
                      // TODO once the database is fix do pop up message to confirm addition to
                      // database
                      ingredientsRepository.addIngredient(
                          Ingredient(name, "NO_ID", false, false),
                          {
                            isDropdownVisible = false
                            val beforeState = state
                            ingredientCurrent = it
                            state =
                                if (isComplete) IngredientInputState.COMPLETE
                                else IngredientInputState.SEMI_COMPLETE
                            action(
                                beforeState,
                                state,
                                IngredientMetaData(quantity, dose, ingredientCurrent))
                          },
                          { Log.e("Fail to add Ingredient : ", " ", it) })
                    })
              }
            }
            // Checked button for validating the ingredient
            if (state == IngredientInputState.SEMI_COMPLETE ||
                state == IngredientInputState.COMPLETE) {
              Spacer(modifier = Modifier.width(8.dp))
              IconButton(
                  modifier = Modifier.padding(top = 4.dp).testTag("CheckIconButton"),
                  onClick = {
                    if (state == IngredientInputState.COMPLETE) {
                      isChecked = true
                    }
                  }) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp))
                  }
            }
          }

          // Second row for the quantity and dose (and delete button)
          Row(
              modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start) {
                // Quantity
                OutlinedTextField(
                    colors = colorOfInputBoxes(state),
                    isError = quantity == 0.0 && state != IngredientInputState.EMPTY,
                    value = if (quantity == 0.0) " " else quantity.toString(),
                    onValueChange = { // Check if the input is a valid number
                      if (it.isNotEmpty() && it.toDoubleOrNull() != null && it.toDouble() >= 0.0) {
                        quantity = it.toDouble()
                        if (quantity != 0.0) {
                          action(
                              state, state, IngredientMetaData(quantity, dose, ingredientCurrent))
                        }
                      }
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f).height(60.dp).testTag("QuantityInput"),
                    placeholder = { Text(text = "...") },
                    label = {
                      Text(
                          text = "Quantity",
                          modifier = Modifier.background(color = Color.Transparent))
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
                          isError =
                              dose == MeasureUnit.EMPTY && state != IngredientInputState.EMPTY,
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
                                          IngredientMetaData(quantity, dose, ingredientCurrent))
                                    }
                                  })
                            }
                          }
                    }
                DeleteButton(state, quantity, dose, name, action)
              }
        }
  }
  HorizontalDivider(modifier = Modifier.padding(top = 4.dp).testTag("IngredientDivider"))
}

/** Composable function for displaying a delete button. */
@Composable
fun DeleteButton(
    state: IngredientInputState,
    quantity: Double,
    dose: MeasureUnit,
    name: String,
    action: (IngredientInputState?, IngredientInputState?, IngredientMetaData?) -> Unit
) {
  // Delete button for removing the ingredient
  if (state == IngredientInputState.SEMI_COMPLETE || state == IngredientInputState.COMPLETE) {
    Spacer(modifier = Modifier.width(8.dp))
    IconButton(
        modifier = Modifier.testTag("DeleteIconButton"),
        onClick = {
          action(
              state,
              IngredientInputState.EMPTY,
              IngredientMetaData(quantity, dose, Ingredient(name, "", false, false)))
        }) {
          Icon(
              imageVector = Icons.Outlined.Close,
              contentDescription = null,
              modifier = Modifier.size(28.dp))
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
