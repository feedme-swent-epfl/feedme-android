package com.android.feedme.ui

import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.viewmodel.InputViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InputViewModelTest {

  private lateinit var viewModel: InputViewModel

  @Before
  fun setup() {
    viewModel = InputViewModel()
  }

  @Test
  fun setNewListWithCompleteIngredient() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    assertEquals(2, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(1, viewModel.totalCompleteIngredientMetadatas.first())
  }

  @Test
  fun setNewListWithSemiCompleteIngredient() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                0.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    assertEquals(2, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(0, viewModel.totalCompleteIngredientMetadatas.first())
  }

  @Test
  fun addNewListWithCompleteIngredient() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    val newList2: MutableList<IngredientMetaData> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.G, Ingredient("Ingredient 2", "ID_TYPE", false, false)),
            IngredientMetaData(
                1.0, MeasureUnit.G, Ingredient("Ingredient 3", "ID_TYPE", false, false)))
    viewModel.addToList(newList2)

    assertEquals(4, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(newList2 + newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(3, viewModel.totalCompleteIngredientMetadatas.first())
  }

  @Test
  fun resetListWithInitialList() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    viewModel.resetList()

    assertEquals(1, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(mutableListOf(null), viewModel.listOfIngredientMetadatas.first())
    assertEquals(0, viewModel.totalCompleteIngredientMetadatas.first())
  }

  @Test
  fun isCompleteCheckForCompleteAndIncompleteIngredient() = runBlocking {
    var isCompleteResult = false

    // Incomplete ingredients
    val incompleteIngredient: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.EMPTY, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(incompleteIngredient)
    viewModel.isComplete { _ -> isCompleteResult = true }
    assertEquals(false, isCompleteResult)

    // Complete ingredients
    val completeIngredient: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.NONE, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(completeIngredient)
    viewModel.isComplete { _ -> isCompleteResult = true }
    assertEquals(true, isCompleteResult)
  }
}
