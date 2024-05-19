package com.android.feedme.ui

import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.viewmodel.CameraViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

class CameraViewModelTest {
  @Test
  fun testUpdateIngredientList() {
    val viewModel = CameraViewModel()
    // Create a mock list of existing ingredients
    val existingIngredients =
        IngredientMetaData(
            2.0, MeasureUnit.TABLESPOON, Ingredient("Sugar", "TEST_ID", false, false))

    viewModel.updateIngredientList(existingIngredients)

    // Assert if necessary
    assertEquals(1, viewModel.listOfIngredientToInput.value.size)
    assertEquals(existingIngredients, viewModel.listOfIngredientToInput.value.first())

    viewModel.updateIngredientList(existingIngredients)
    val newIngredients =
        IngredientMetaData(
            4.0, MeasureUnit.TABLESPOON, Ingredient("Sugar", "TEST_ID", false, false))

    // Assert if necessary
    assertEquals(1, viewModel.listOfIngredientToInput.value.size)
    assertEquals(newIngredients, viewModel.listOfIngredientToInput.value.first())
  }
}
