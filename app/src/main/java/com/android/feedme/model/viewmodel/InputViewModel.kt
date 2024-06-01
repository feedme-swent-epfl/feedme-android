package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.ui.component.IngredientInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for managing input related to ingredients. Keeps track of the total number of
 * ingredients entries to display, the list of [IngredientMetaData], and the number of complete
 * [IngredientMetaData].
 */
class InputViewModel : ViewModel() {
  // StateFlow to hold the total number of ingredients to display
  private val _totalIngredientEntriesDisplayed = MutableStateFlow(1)
  val totalIngredientEntriesDisplayed: StateFlow<Int> = _totalIngredientEntriesDisplayed

  // StateFlow to hold the list of ingredient
  private val _listOfIngredientMetadatas = MutableStateFlow<List<IngredientMetaData?>>(listOf(null))
  val listOfIngredientMetadatas: StateFlow<List<IngredientMetaData?>> = _listOfIngredientMetadatas

  // StateFlow to hold the total number of complete ingredients
  private val _totalCompleteIngredientMetadatas = MutableStateFlow(0)
  val totalCompleteIngredientMetadatas: StateFlow<Int> = _totalCompleteIngredientMetadatas

  // StateFlow to hold the total number of complete ingredients
  private val _isComplete =
      MutableStateFlow(
          _listOfIngredientMetadatas.value.count {
            it != null &&
                it.measure != MeasureUnit.EMPTY &&
                it.quantity != 0.0 &&
                it.ingredient.name.isNotBlank() &&
                it.ingredient.id != "NO_ID" &&
                it.ingredient.id != ""
          } == _totalIngredientEntriesDisplayed.value - 1)
  val isComplete: StateFlow<Boolean> = _isComplete
  /**
   * Sets a new list of [IngredientMetaData] and adds a empty entry to add a new ingredient in case.
   *
   * @param newList The new list of [IngredientMetaData].
   */
  fun setNewList(newList: MutableList<IngredientMetaData?>) {
    newList.add(null)
    _listOfIngredientMetadatas.value = newList
    _totalIngredientEntriesDisplayed.value = newList.size
    _totalCompleteIngredientMetadatas.value +=
        newList.count {
          it != null &&
              it.measure != MeasureUnit.EMPTY &&
              it.quantity != 0.0 &&
              it.ingredient.name.isNotBlank() &&
              it.ingredient.id != "NO_ID" &&
              it.ingredient.id != ""
        }
  }

  /**
   * Add a new list of ingredients to the one already exisiting.
   *
   * @param newList The new list of [IngredientMetaData].
   */
  fun addToList(newList: MutableList<IngredientMetaData>) {
    _listOfIngredientMetadatas.value = newList.plus(_listOfIngredientMetadatas.value)
    _totalIngredientEntriesDisplayed.value += newList.size
    _totalCompleteIngredientMetadatas.value +=
        newList.count {
          it != null &&
              it.measure != MeasureUnit.EMPTY &&
              it.quantity != 0.0 &&
              it.ingredient.name.isNotBlank() &&
              it.ingredient.id != "NO_ID" &&
              it.ingredient.id != ""
        }
  }

  /** Resets the list of ingredients and adds a empty entry to add a new ingredient */
  fun resetList() {
    _listOfIngredientMetadatas.value = listOf(null)
    _totalIngredientEntriesDisplayed.value = 1
    _totalCompleteIngredientMetadatas.value = 0
  }

  /**
   * Updates the ingredient at the specified index with the new ingredient data. Uses the transition
   * of the states to determine what to do with the ingredient in the entry
   *
   * @param index The index of the [IngredientMetaData] to update.
   * @param before The previous state of the [IngredientMetaData] input.
   * @param now The current state of the [IngredientMetaData] input.
   * @param newIngredient The new [IngredientMetaData].
   */
  fun updateListElementBehaviour(
      index: Int,
      before: IngredientInputState?,
      now: IngredientInputState?,
      newIngredient: IngredientMetaData?
  ) {
    val newList = _listOfIngredientMetadatas.value.toMutableList()
    val wasComplete = before == IngredientInputState.COMPLETE
    val isComplete = now == IngredientInputState.COMPLETE

    if (wasComplete != isComplete) {
      _totalCompleteIngredientMetadatas.value += if (isComplete) 1 else -1
    }
    newList[index] = newIngredient
    if (now == IngredientInputState.SEMI_COMPLETE && before == IngredientInputState.EMPTY) {
      newList.add(null)
      _totalIngredientEntriesDisplayed.value += 1
    } else if (now == IngredientInputState.EMPTY && before != IngredientInputState.EMPTY) {
      newList.removeAt(index)
      _totalIngredientEntriesDisplayed.value -= 1
    }
    _listOfIngredientMetadatas.value = newList
    _isComplete.value =
        _totalIngredientEntriesDisplayed.value - 1 == _totalCompleteIngredientMetadatas.value
  }

  /**
   * Checks if all ingredients are complete and invokes the onComplete callback if true.
   *
   * @param onComplete Callback function to be invoked when all [IngredientMetaData] are completed.
   */
  fun isComplete(onComplete: (List<IngredientMetaData?>) -> Unit) {
    _isComplete.value =
        _listOfIngredientMetadatas.value.count {
          it != null &&
              it.measure != MeasureUnit.EMPTY &&
              it.quantity != 0.0 &&
              it.ingredient.name.isNotBlank() &&
              it.ingredient.id != "NO_ID" &&
              it.ingredient.id != ""
        } == _totalIngredientEntriesDisplayed.value - 1
    if (isComplete.value) {
      onComplete(_listOfIngredientMetadatas.value)
    }
  }
}
