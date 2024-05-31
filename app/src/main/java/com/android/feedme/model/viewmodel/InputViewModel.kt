package com.android.feedme.model.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.ui.component.IngredientInputState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for managing input related to ingredients. Keeps track of the total number of
 * ingredients entries to display, the list of [IngredientMetaData], and the number of complete
 * [IngredientMetaData].
 */
class InputViewModel(val context: Context? = null) : ViewModel() {
  // SharedPreferences name
  private val PREFS_NAME = "InputViewModelPrefs"

  // SharedPreferences keys
  private val LIST_KEY_CURRENT = "listOfIngredientMetadatas"
  private val LIST_KEY_FRIDGE = "fridge"

  // StateFlow to hold the total number of ingredients to display
  private val _totalIngredientEntriesDisplayed = MutableStateFlow(1)
  val totalIngredientEntriesDisplayed: StateFlow<Int> = _totalIngredientEntriesDisplayed

  // StateFlow to hold the list of ingredient
  private val _listOfIngredientMetadatas = MutableStateFlow<List<IngredientMetaData?>>(listOf(null))
  val listOfIngredientMetadatas: StateFlow<List<IngredientMetaData?>> = _listOfIngredientMetadatas

  // StateFlow to hold the total number of complete ingredients
  private val _totalCompleteIngredientMetadatas = MutableStateFlow(0)
  val totalCompleteIngredientMetadatas: StateFlow<Int> = _totalCompleteIngredientMetadatas

  // StateFlow to hold the list of ingredient in fridge
  private val _fridge = MutableStateFlow<List<IngredientMetaData?>>(listOf(null))
  val fridge: StateFlow<List<IngredientMetaData?>> = _fridge

  // StateFlow to hold the boolean if the current state was saved
  private val _wasSaved = MutableStateFlow(_fridge == _totalCompleteIngredientMetadatas)
  val wasSaved: StateFlow<Boolean> = _wasSaved

  init {
    retrieveSavedList()
  }

  // Function to save the list when ViewModel is about to be destroyed
  override fun onCleared() {
    super.onCleared()
    saveList()
  }

  /** Saves the current list of ingredients to the fridge. */
  fun saveInFridge() {
    _fridge.value = _listOfIngredientMetadatas.value.ifEmpty { listOf<IngredientMetaData?>(null) }
    wasSaved()
  }

  /** Checks if the list of ingredients was saved in the fridge. */
  fun wasSaved() {
    _wasSaved.value = _fridge.value == _listOfIngredientMetadatas.value
  }

  /** Loads the list of ingredients from the fridge. */
  fun loadFridge() {
    setNewList(_fridge.value.toMutableList())
  }

  private fun saveList() {
    if (context != null) {
      val gson = Gson()
      val type = object : TypeToken<List<IngredientMetaData?>>() {}.type
      val json =
          gson.toJson(
              _listOfIngredientMetadatas.value.ifEmpty { listOf<IngredientMetaData?>(null) }, type)
      val jsonFridge =
          gson.toJson(_fridge.value.ifEmpty { listOf<IngredientMetaData?>(null) }, type)

      val masterKeyAlias =
          MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

      val prefs =
          EncryptedSharedPreferences.create(
              context,
              PREFS_NAME,
              masterKeyAlias,
              EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
              EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

      prefs.edit().putString(LIST_KEY_CURRENT, json).apply()
      prefs.edit().putString(LIST_KEY_FRIDGE, jsonFridge).apply()
      Log.d("InputViewModel", "Json sent")
    } else {
      Log.e(
          "InputViewModel",
          "Error: Unable to save current ingredient list because no 'Context' was given")
    }
    _fridge.value = _fridge.value.ifEmpty { listOf<IngredientMetaData?>(null) }
    _listOfIngredientMetadatas.value =
        _listOfIngredientMetadatas.value.ifEmpty { listOf<IngredientMetaData?>(null) }
    wasSaved()
  }

  fun retrieveSavedList() {
    if (context != null) {
      val masterKeyAlias =
          MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

      val prefs =
          EncryptedSharedPreferences.create(
              context,
              PREFS_NAME,
              masterKeyAlias,
              EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
              EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

      val json = prefs.getString(LIST_KEY_CURRENT, "[]")
      val jsonFridge = prefs.getString(LIST_KEY_FRIDGE, "[]")
      val gson = Gson()
      val type = object : TypeToken<List<IngredientMetaData?>>() {}.type

      if (jsonFridge != null) {
        val listFridge = gson.fromJson<List<IngredientMetaData?>>(jsonFridge, type)
        _fridge.value = listFridge.ifEmpty { listOf<IngredientMetaData?>(null) }
      } else {
        _fridge.value = listOf<IngredientMetaData?>(null)
      }

      if (json != null) {
        val list = gson.fromJson<List<IngredientMetaData?>>(json, type)
        _listOfIngredientMetadatas.value = list.ifEmpty { listOf<IngredientMetaData?>(null) }
        _totalIngredientEntriesDisplayed.value = _listOfIngredientMetadatas.value.size
        _totalCompleteIngredientMetadatas.value =
            list.count { it != null && it.measure != MeasureUnit.EMPTY && it.quantity != 0.0 }
        Log.d("InputViewModel", "Json received")
      } else {
        Log.e(
            "InputViewModel",
            "Error: Unable to retrieve current ingredient list because no data was found")
      }
    } else {
      Log.e(
          "InputViewModel",
          "Error: Unable to retrieve current ingredient list because no 'Context' was given")
    }
    _fridge.value = _fridge.value.ifEmpty { listOf<IngredientMetaData?>(null) }
    _listOfIngredientMetadatas.value =
        _listOfIngredientMetadatas.value.ifEmpty { listOf<IngredientMetaData?>(null) }
    _totalIngredientEntriesDisplayed.value = _listOfIngredientMetadatas.value.size
  }

  /**
   * Sets a new list of [IngredientMetaData] and adds a empty entry to add a new ingredient in case.
   *
   * @param newList The new list of [IngredientMetaData].
   */
  fun setNewList(newList: MutableList<IngredientMetaData?>) {
    _listOfIngredientMetadatas.value = newList.ifEmpty { listOf<IngredientMetaData?>(null) }
    _listOfIngredientMetadatas.value = newList
    _totalIngredientEntriesDisplayed.value = newList.size
    _totalCompleteIngredientMetadatas.value +=
        newList.count { it != null && it.measure != MeasureUnit.EMPTY && it.quantity != 0.0 }
    saveList()
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
        newList.count { it.measure != MeasureUnit.EMPTY && it.quantity != 0.0 }
    saveList()
  }

  /** Resets the list of ingredients and adds a empty entry to add a new ingredient */
  fun resetList() {
    _listOfIngredientMetadatas.value = listOf(null)
    _totalIngredientEntriesDisplayed.value = 1
    _totalCompleteIngredientMetadatas.value = 0
    saveList()
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
    if (now != IngredientInputState.EMPTY && before == IngredientInputState.EMPTY) {
      newList.add(null)
      _totalIngredientEntriesDisplayed.value += 1
    } else if (now == IngredientInputState.EMPTY && before != IngredientInputState.EMPTY) {
      newList.removeAt(index)
      _totalIngredientEntriesDisplayed.value -= 1
    }
    _listOfIngredientMetadatas.value = newList
    saveList()
  }

  /**
   * Checks if all ingredients are complete and invokes the onComplete callback if true.
   *
   * @param onComplete Callback function to be invoked when all [IngredientMetaData] are completed.
   */
  fun isComplete(onComplete: (List<IngredientMetaData?>) -> Unit) {
    if (_totalIngredientEntriesDisplayed.value - 1 == _totalCompleteIngredientMetadatas.value) {
      onComplete(_listOfIngredientMetadatas.value)
    }
  }
}
