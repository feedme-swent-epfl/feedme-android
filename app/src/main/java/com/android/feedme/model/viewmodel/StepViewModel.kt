package com.android.feedme.model.viewmodel

import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.Step
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecipeStepViewModel : ViewModel() {
  // StateFlow to hold the list of steps
  private val _steps = MutableStateFlow<List<Step>>(emptyList())
  val steps: StateFlow<List<Step>> = _steps

  /**
   * Add a new step to the list of steps.
   *
   * @param step Step to add to the list.
   */
  fun addStep(step: Step) {
    val newList = _steps.value.toMutableList()
    newList.add(step)
    _steps.value = newList
  }

  /**
   * Update a step at a specific index.
   *
   * @param index Index of the step to update.
   * @param newStep New step to replace the old step.
   */
  fun updateStep(index: Int, newStep: Step) {
    val newList = _steps.value.toMutableList()
    if (index >= 0 && index < newList.size) {
      newList[index] = newStep
    }
    _steps.value = newList
  }

  /**
   * Delete a step from the list of steps.
   *
   * @param step Step to delete from the list.
   */
  fun deleteStep(step: Step) {
    var newList = _steps.value.toMutableList()
    newList.remove(step)
    _steps.value = newList

    renumberSteps()
  }

  /** Re-number the steps in the list. */
  private fun renumberSteps() {
    val newList = _steps.value.toMutableList()
    var i = 1
    newList.forEach {
      it.stepNumber = i
      i++
    }
    _steps.value = newList
  }

  /** Reset the list of steps to an empty list. */
  fun resetSteps() {
    _steps.value = emptyList()
  }
}
