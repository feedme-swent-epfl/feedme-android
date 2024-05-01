package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.ui.component.IngredientInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for managing input related to ingredients. Keeps track of the total number of
 * ingredients, the list of ingredients, and the number of complete ingredients.
 */
class InputViewModel : ViewModel() {
  // StateFlow to hold the total number of ingredients to display
  private val _totalIngredients = MutableStateFlow(1)
  val totalIngredients: StateFlow<Int> = _totalIngredients

  // StateFlow to hold the list of ingredient
  private val _listOfIngredients = MutableStateFlow<List<IngredientMetaData?>>(listOf(null))
  val listOfIngredients: StateFlow<List<IngredientMetaData?>> = _listOfIngredients

  // StateFlow to hold the total number of complete ingredients
  private val _totalCompleteIngredients = MutableStateFlow(0)
  val totalCompleteIngredients: StateFlow<Int> = _totalCompleteIngredients

  /**
   * Sets a new list of ingredients.
   *
   * @param newList The new list of ingredients.
   */
  fun setNewList(newList: MutableList<IngredientMetaData?>) {
    newList.add(null)
    _listOfIngredients.value = newList
    _totalIngredients.value = newList.size
    _totalCompleteIngredients.value +=
        newList.count { it != null && it.measure != MeasureUnit.EMPTY && it.quantity != 0.0 }
  }

  /**
   * Add a new list of ingredients to the one already exisiting.
   *
   * @param newList The new list of ingredients.
   */
  fun addToList(newList: MutableList<IngredientMetaData>) {
    _listOfIngredients.value = newList.plus(_listOfIngredients.value)
    _totalIngredients.value += newList.size
    _totalCompleteIngredients.value +=
        newList.count { it.measure != MeasureUnit.EMPTY && it.quantity != 0.0 }
  }

  /** Resets the list of ingredients. */
  fun resetList() {
    _listOfIngredients.value = listOf(null)
    _totalIngredients.value = 1
    _totalCompleteIngredients.value = 0
  }

  /**
   * Updates the ingredient at the specified index with the new ingredient data.
   *
   * @param index The index of the ingredient to update.
   * @param before The previous state of the ingredient input.
   * @param now The current state of the ingredient input.
   * @param newIngredient The new ingredient data.
   */
  fun setUpdate(
      index: Int,
      before: IngredientInputState?,
      now: IngredientInputState?,
      newIngredient: IngredientMetaData?
  ) {
    val newList = _listOfIngredients.value.toMutableList()
    val wasComplete = before == IngredientInputState.COMPLETE
    val isComplete = now == IngredientInputState.COMPLETE

    if (wasComplete != isComplete) {
      _totalCompleteIngredients.value += if (isComplete) 1 else -1
    }
    newList[index] = newIngredient
    if (now == IngredientInputState.SEMI_COMPLETE && before == IngredientInputState.EMPTY) {
      newList.add(null)
      _totalIngredients.value += 1
    } else if (now == IngredientInputState.EMPTY && before != IngredientInputState.EMPTY) {
      newList.removeAt(index)
      _totalIngredients.value -= 1
    }
    _listOfIngredients.value = newList
  }

  /**
   * Checks if all ingredients are complete and invokes the onComplete callback if true.
   *
   * @param onComplete Callback function to be invoked when all ingredients are complete.
   */
  fun isComplete(onComplete: (List<IngredientMetaData?>) -> Unit) {
    if (_totalIngredients.value - 1 == _totalCompleteIngredients.value) {
      onComplete(_listOfIngredients.value)
    }
  }
}
