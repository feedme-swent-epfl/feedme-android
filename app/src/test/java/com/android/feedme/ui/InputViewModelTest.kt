package com.android.feedme.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
                200.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
    viewModel.setNewList(newList)
    assertEquals(2, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(true, viewModel.isComplete.value)
  }

  @Test
  fun setNewListWithSemiCompleteIngredient() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                0.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
    viewModel.setNewList(newList)
    assertEquals(2, viewModel.totalIngredientEntriesDisplayed.first())
    assertEquals(newList, viewModel.listOfIngredientMetadatas.first())
    assertEquals(false, viewModel.isComplete.value)
  }

  @Test
  fun addNewListWithCompleteIngredient() = runBlocking {
    val newList: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
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
    assertEquals(true, viewModel.isComplete.value)
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
    assertEquals(true, viewModel.isComplete.value)
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
    viewModel.isListComplete { _ -> isCompleteResult = true }
    assertEquals(false, isCompleteResult)

    // Complete ingredients
    val completeIngredient: MutableList<IngredientMetaData?> =
        mutableListOf(
            IngredientMetaData(
                1.0, MeasureUnit.NONE, Ingredient("Ingredient 1", "ID_TYPE", false, false)),
            null)
    viewModel.setNewList(completeIngredient)
    viewModel.isListComplete { _ -> isCompleteResult = true }
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

    viewModel.updateListElementBehaviour(0, true, false, null)

    assertEquals(true, viewModel.isComplete.value)
    assertEquals(1, viewModel.totalIngredientEntriesDisplayed.first())

    viewModel.updateListElementBehaviour(
        0,
        false,
        true,
        IngredientMetaData(
            100.0, MeasureUnit.G, Ingredient("Ingredient 1", "ID_TYPE", false, false)))

    assertEquals(true, viewModel.isComplete.value)
    assertEquals(2, viewModel.totalIngredientEntriesDisplayed.first())
  }

  @Test
  fun testCompleteIngredient() {
    val completeIngredient =
        IngredientMetaData(
            quantity = 200.0,
            measure = MeasureUnit.G,
            ingredient = Ingredient("Sugar", "ID_001", false, false))
    assertEquals(true, viewModel.isCompleteIngredient(completeIngredient))
  }

  @Test
  fun testIncompleteIngredient_Null() {
    val incompleteIngredient: IngredientMetaData? = null
    assertEquals(false, viewModel.isCompleteIngredient(incompleteIngredient))
  }

  @Test
  fun testIncompleteIngredient_EmptyMeasure() {
    val incompleteIngredient =
        IngredientMetaData(
            quantity = 200.0,
            measure = MeasureUnit.EMPTY,
            ingredient = Ingredient("Sugar", "ID_001", false, false))
    assertEquals(false, viewModel.isCompleteIngredient(incompleteIngredient))
  }

  @Test
  fun testIncompleteIngredient_ZeroQuantity() {
    val incompleteIngredient =
        IngredientMetaData(
            quantity = 0.0,
            measure = MeasureUnit.G,
            ingredient = Ingredient("Sugar", "ID_001", false, false))
    assertEquals(false, viewModel.isCompleteIngredient(incompleteIngredient))
  }

  @Test
  fun testIncompleteIngredient_BlankName() {
    val incompleteIngredient =
        IngredientMetaData(
            quantity = 200.0,
            measure = MeasureUnit.G,
            ingredient = Ingredient(" ", "ID_001", false, false))
    assertEquals(false, viewModel.isCompleteIngredient(incompleteIngredient))
  }

  @Test
  fun testIncompleteIngredient_NoId() {
    val incompleteIngredient =
        IngredientMetaData(
            quantity = 200.0,
            measure = MeasureUnit.G,
            ingredient = Ingredient("Sugar", "NO_ID", false, false))
    assertEquals(false, viewModel.isCompleteIngredient(incompleteIngredient))
  }

  @Test
  fun testIncompleteIngredient_EmptyId() {
    val incompleteIngredient =
        IngredientMetaData(
            quantity = 200.0,
            measure = MeasureUnit.G,
            ingredient = Ingredient("Sugar", "", false, false))
    assertEquals(false, viewModel.isCompleteIngredient(incompleteIngredient))
  }
}
