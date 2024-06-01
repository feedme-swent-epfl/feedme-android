package com.android.feedme.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.ui.component.IngredientInputState
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
  private lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    viewModel = InputViewModel()
  }

  @Test
  fun setNewListWithCompleteIngredient() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    assertEquals(1, viewModel.totalIngredientEntriesDisplayed.first())
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
    assertEquals(1, viewModel.totalIngredientEntriesDisplayed.first())
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

    assertEquals(3, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(newList2 + newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(3, viewModel.totalCompleteIngredientMetadatas.first())
  }

  @Test
  fun resetListWithInitialList() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
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
                1.0, MeasureUnit.EMPTY, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
    viewModel.setNewList(incompleteIngredient)
    viewModel.isComplete { _ -> isCompleteResult = true }
    assertEquals(false, isCompleteResult)

    // Complete ingredients
    val completeIngredient: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.NONE, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
    viewModel.setNewList(completeIngredient)
    viewModel.isComplete { _ -> isCompleteResult = true }
    assertEquals(true, isCompleteResult)
  }

  @Test
  fun saveInFridgeTest() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    viewModel.saveInFridge()

    assertEquals(newList, viewModel.fridge.first())
    assertEquals(true, viewModel.wasSaved.first())
  }

  @Test
  fun loadFridgeTest() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    viewModel.saveInFridge()

    viewModel.setNewList(mutableListOf())
    viewModel.loadFridge()

    assertEquals(newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(1, viewModel.totalIngredientEntriesDisplayed.first())
  }

  @Test
  fun wasSavedTest() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))
    viewModel.setNewList(newList)
    viewModel.saveInFridge()
    viewModel.wasSaved()

    assertEquals(true, viewModel.wasSaved.first())

    val anotherList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                100.0, MeasureUnit.G, Ingredient("Ingredient 2", "ID_TYPE", false, false)))
    viewModel.setNewList(anotherList)
    viewModel.wasSaved()

    assertEquals(false, viewModel.wasSaved.first())
  }

  @Test
  fun updateListElementBehaviourTest() = runBlocking {
    val initialList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                100.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
    viewModel.setNewList(initialList)

    viewModel.updateListElementBehaviour(
        0, IngredientInputState.COMPLETE, IngredientInputState.EMPTY, null)

    assertEquals(0, viewModel.totalCompleteIngredientMetadatas.first())
    assertEquals(1, viewModel.totalIngredientEntriesDisplayed.first())

    viewModel.updateListElementBehaviour(
        0,
        IngredientInputState.EMPTY,
        IngredientInputState.COMPLETE,
        IngredientMetaData(
            100.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))

    assertEquals(1, viewModel.totalCompleteIngredientMetadatas.first())
    assertEquals(2, viewModel.totalIngredientEntriesDisplayed.first())
  }
}
