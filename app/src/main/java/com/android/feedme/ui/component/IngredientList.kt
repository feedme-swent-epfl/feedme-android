package com.android.feedme.ui.component

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.ui.theme.InValidInput
import com.android.feedme.ui.theme.NoInput
import com.android.feedme.ui.theme.ValidInput

@Composable
fun IngredientList(
    modifier: Modifier = Modifier,
    list: MutableList<IngredientMetaData>? = null,
) {
    val totalIngredients = remember { mutableIntStateOf(1 + (list?.size ?: 0)) }
    val listOfIngredients = remember { mutableStateListOf<IngredientMetaData?>() }

    //If (totalCompleteIngredients == totalIngredients - 1) then all ingredients are Complete
    val totalCompleteIngredients = remember { mutableIntStateOf(0) }


    list?.let { listOfIngredients.addAll(it) }
    listOfIngredients.add(null)

    LazyColumn (
        modifier = modifier
    ){
        this.
        items(totalIngredients.intValue) { index ->
            val movableContent = movableContentOf {
                IngredientInput(listOfIngredients[index]) { before, now, newIngredient ->
                    if (now != IngredientInputState.COMPLETE && before == IngredientInputState.COMPLETE) {
                        listOfIngredients[index] = newIngredient
                        totalCompleteIngredients.intValue -= 1
                    }
                    if (now == IngredientInputState.COMPLETE && before == IngredientInputState.SEMI_COMPLETE) {
                        listOfIngredients[index] = newIngredient
                        totalCompleteIngredients.intValue += 1
                    }
                    if (now == IngredientInputState.SEMI_COMPLETE && before == IngredientInputState.SEMI_COMPLETE) {
                        listOfIngredients[index] = newIngredient
                    }
                    if (now == IngredientInputState.SEMI_COMPLETE && before == IngredientInputState.EMPTY) {
                        listOfIngredients[index] = newIngredient
                        listOfIngredients.add(null)
                        totalIngredients.intValue += 1
                    }
                    if (now == IngredientInputState.EMPTY && before != IngredientInputState.EMPTY) {
                        listOfIngredients.removeAt(index)
                        totalIngredients.intValue -= 1
                    }
                }
            }
            movableContent()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientInput(
    ingredient: IngredientMetaData? = null,
    action: (IngredientInputState?, IngredientInputState?, IngredientMetaData?) -> Unit
) {
    var name by remember { mutableStateOf(ingredient?.ingredient?.name ?: " ") }
    var quantity by remember { mutableDoubleStateOf(ingredient?.quantity ?: 0.0) }
    var dose by remember { mutableStateOf(ingredient?.measure ?: MeasureUnit.EMPTY) }

    val isComplete by remember { mutableStateOf(name.isNotBlank() && dose != MeasureUnit.EMPTY && quantity != 0.0) }
    var state by remember { mutableStateOf(if(isComplete) IngredientInputState.COMPLETE else if(ingredient != null) IngredientInputState.SEMI_COMPLETE else  IngredientInputState.EMPTY) }

    var isDropdownVisible by remember { mutableStateOf(false) }
    val suggestionIngredients = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5") // Your list of items



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically) {


        // Ingredients Box
        Box(modifier = Modifier.weight(1.5f).height(55.dp)) {
            OutlinedTextField(
                colors = colorOfInputBoxes(state),
                value = name,
                isError = name == " " && state != IngredientInputState.EMPTY,
                onValueChange = {
                    name = it
                    isDropdownVisible = true

                },
                singleLine = true,
                modifier = Modifier.padding(end = 0.dp),
                placeholder = { Text(text = "...") },
                label =  { Text(text = "Ingredient", modifier = Modifier.background(color = Color.Transparent))
                })

            DropdownMenu(
                modifier = Modifier
                    .height(120.dp),
                expanded = isDropdownVisible && name.isNotEmpty(),
                onDismissRequest = { isDropdownVisible = false },
                properties =
                PopupProperties(
                    focusable = false, dismissOnClickOutside = false, dismissOnBackPress = false),
            ) {
                suggestionIngredients.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            name = item
                            isDropdownVisible = false
                            val beforeState = state
                            if (name != " ") {
                                state = if (isComplete) IngredientInputState.COMPLETE else IngredientInputState.SEMI_COMPLETE
                                action(
                                    beforeState,
                                    state,
                                    IngredientMetaData(quantity, dose, Ingredient(name, "", ""))
                                )
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
                if ((it.isEmpty() || it.toDoubleOrNull() != null) && it.toDouble() >= 0.0 ) {
                    quantity = it.toDouble()
                    if (quantity != 0.0) {
                        action(
                            state,
                            state,
                            IngredientMetaData(quantity, dose, Ingredient(name, "", ""))
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.weight(1f).height(55.dp),
            placeholder = { Text(text = "...")},
            label = { Text(text = "Quantity", modifier= Modifier.background(color = Color.Transparent))}
        )



        Spacer(modifier = Modifier.width(8.dp))







        // Dose
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f).height(55.dp),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                colors = colorOfInputBoxes(state),
                isError = dose == MeasureUnit.EMPTY && state != IngredientInputState.EMPTY,
                readOnly = true,
                value = if (dose != MeasureUnit.EMPTY) dose.toString() else " ",
                onValueChange = {
                    expanded = expanded
                },
                label = { Text("Dose") },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                modifier = Modifier.height(120.dp),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false

                }
            ) {
                MeasureUnit.values().forEach { selectionOption ->
                    DropdownMenuItem(
                        text = {Text(text = selectionOption.toString())},
                        onClick = {
                            dose = selectionOption
                            expanded = false
                            if (dose != MeasureUnit.EMPTY) {
                                val beforeState = state
                                state = if (isComplete) IngredientInputState.COMPLETE else IngredientInputState.SEMI_COMPLETE
                                action(
                                    beforeState,
                                    state,
                                    IngredientMetaData(quantity, dose, Ingredient(name, "", ""))
                                )
                            }
                        }
                    )
                }
            }
        }


        if (state == IngredientInputState.SEMI_COMPLETE || state == IngredientInputState.COMPLETE) {
            IconButton(onClick = { action(state, IngredientInputState.EMPTY, IngredientMetaData(quantity, dose, Ingredient(name, "", ""))) }) {
                Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).height(55.dp))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun colorOfInputBoxes(state: IngredientInputState): TextFieldColors {
    return ExposedDropdownMenuDefaults.textFieldColors(unfocusedContainerColor =  if (state != IngredientInputState.EMPTY) ValidInput else NoInput, focusedContainerColor = if (state != IngredientInputState.EMPTY) ValidInput else NoInput, errorContainerColor = InValidInput )

}

enum class IngredientInputState {
    EMPTY,
    SEMI_COMPLETE,
    COMPLETE
}