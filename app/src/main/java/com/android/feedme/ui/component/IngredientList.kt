package com.android.feedme.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit

@Composable
fun IngredientList(
    modifier: Modifier = Modifier,
    list: MutableList<IngredientMetaData>? = null,
) {
    val listOfIngredients = remember { mutableStateListOf<IngredientMetaData?>() }
    list?.let { listOfIngredients.addAll(it) }
    listOfIngredients.add(null)

    LazyColumn (
        modifier = modifier
    ){
        this.
        itemsIndexed(listOfIngredients) { index, ingredient ->
            val movableContent = movableContentOf {
                IngredientInput(ingredient) { before, now, s ->
                    if (now == IngredientInputState.SEMI_COMPLETE && before == IngredientInputState.SEMI_COMPLETE) {
                        listOfIngredients[index] = s
                    }
                    if (now == IngredientInputState.SEMI_COMPLETE && before == IngredientInputState.EMPTY) {
                        listOfIngredients[index] = s
                        listOfIngredients.add(null)
                    }
                    if (now == IngredientInputState.EMPTY && before != IngredientInputState.EMPTY) {
                        listOfIngredients.removeAt(index)
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
    var name by remember { mutableStateOf(ingredient?.ingredient?.name ?: "") }
    var quantity by remember { mutableDoubleStateOf(ingredient?.quantity ?: 0.0) }
    var dose by remember { mutableStateOf(ingredient?.measure ?: MeasureUnit.EMPTY) }
    var state by remember { mutableStateOf(if(ingredient != null) IngredientInputState.SEMI_COMPLETE else IngredientInputState.EMPTY) }


    var isDropdownVisible by remember { mutableStateOf(false) }
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5") // Your list of items

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1.5f).height(55.dp)) {
            // Ingredients
            OutlinedTextField(
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                value = name,
                onValueChange = {
                    name = it
                    isDropdownVisible = true

                },
                singleLine = true,
                modifier = Modifier.padding(end = 0.dp),
                placeholder = { Text(text = "Ingredient") })

            DropdownMenu(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .height(120.dp),
                expanded = isDropdownVisible && name.isNotEmpty(),
                onDismissRequest = { isDropdownVisible = false },
                properties =
                PopupProperties(
                    focusable = false, dismissOnClickOutside = false, dismissOnBackPress = false),
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            name = item
                            isDropdownVisible = false
                            val beforeState = state
                            if (name != "") {
                                state = IngredientInputState.SEMI_COMPLETE
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

        // Dose
        OutlinedTextField(
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            value = quantity.toString(),
            onValueChange = { // Check if the input is a valid number
                if (it.isEmpty() || it.toDoubleOrNull() != null) {
                    quantity = it.toDouble()
                    if (quantity != 0.0) {
                        val beforeState = state
                        state = IngredientInputState.SEMI_COMPLETE
                        action(
                            beforeState,
                            state,
                            IngredientMetaData(quantity, dose, Ingredient(name, "", ""))
                        )
                    }
                }


            },
            singleLine = true,
            modifier = Modifier.weight(1f).height(55.dp),
            placeholder = { Text(text = "Dose") })

        Spacer(modifier = Modifier.width(8.dp))

        var expanded by remember { mutableStateOf(false) }

        // Dropdown for dose measurement type
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f).height(50.dp),
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                readOnly = true,
                value = dose.toString(),
                onValueChange = {
                    expanded = expanded

                },
                label = { Text("Dose") },

                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
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
                            val beforeState = state
                            if (!(dose == MeasureUnit.EMPTY)) {
                                val beforeState = state
                                state = IngredientInputState.SEMI_COMPLETE
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

enum class IngredientInputState {
    EMPTY,
    SEMI_COMPLETE,
    COMPLETE
}
