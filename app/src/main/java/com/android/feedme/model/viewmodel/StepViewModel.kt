package com.android.feedme.model.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.feedme.model.data.Step
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecipeStepViewModel : ViewModel() {
  // StateFlow to hold the list of steps
  private val _steps = MutableStateFlow<List<Step>>(emptyList())
  val steps: StateFlow<List<Step>> = _steps

  // Add a new step to the list
  fun addStep(step: Step) {
    val newList = _steps.value.toMutableList()
    newList.add(step)
    _steps.value = newList
  }

  // Update a step at a specific index
  fun updateStep(index: Int, newStep: Step) {
    val newList = _steps.value.toMutableList()
    if (index >= 0 && index < newList.size) {
      newList[index] = newStep
    }
    _steps.value = newList
  }

  // Delete a step at a specific index
  // Use index directly to delete a step
  // Method to delete a step by index
  fun deleteStep(step: Step) {
    var newList = _steps.value.toMutableList()
    Log.d("RecipeStepViewModel", "current list: $newList")
    newList.remove(step)
    Log.d("RecipeStepViewModel", "Step deleted: $newList")
    _steps.value = newList

    renumberSteps()
  }

  private fun renumberSteps() {
    Log.d("RecipeStepViewModel", "Renumbering steps")
    Log.d("RecipeStepViewModel", "current list: ${_steps.value}")
    val newList = _steps.value.toMutableList()
    var i = 1
    newList.forEach {
      it.stepNumber = i
      i++
    }
    Log.d("RecipeStepViewModel", "Renumbered list: $newList")
    _steps.value = newList
    Log.d("RecipeStepViewModel", "Renumbered list: ${_steps.value}")
  }

  // Reset the list of steps to empty
  fun resetSteps() {
    _steps.value = emptyList()
  }
}
