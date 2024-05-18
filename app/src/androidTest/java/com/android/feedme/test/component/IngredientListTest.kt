package com.android.feedme.test.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.ui.component.IngredientList
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IngredientListTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testIngredientListTestEverythingDisplayed() {
    val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
    ProfileRepository.initialize(mockFirestore)
    RecipeRepository.initialize(mockFirestore)
    IngredientsRepository.initialize(mockFirestore)
    composeTestRule.setContent { IngredientList() }

    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuantityInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoseBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoseInput").assertIsDisplayed()
  }

  @Test
  fun testIngredientDropDownMenuWorksAndHasClickableItem() {
    val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
    val mockIngredientsRepository = mockk<IngredientsRepository>()

    ProfileRepository.initialize(mockFirestore)
    RecipeRepository.initialize(mockFirestore)
    IngredientsRepository.initialize(mockIngredientsRepository)

    val ingredient = Ingredient("Sugar", "sugarId", false, false)
    val expectedIngredientsList = listOf(ingredient)
    every { mockIngredientsRepository.getFilteredIngredients(any(), any(), any()) } answers
        {
          val onSuccess: (List<Ingredient>) -> Unit = arg(1)
          onSuccess.invoke(expectedIngredientsList)
        }
    every { mockIngredientsRepository.fetchIngredients(any(), any(), any()) } answers
        {
          val onSuccess: (List<Ingredient>) -> Unit = arg(1)
          onSuccess.invoke(expectedIngredientsList)
        }
    composeTestRule.setContent { IngredientList() }

    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("IngredientsInput")
        .assertIsDisplayed()
        .performClick()
        .performTextInput("Test")
    composeTestRule.onNodeWithTag("AddOption").assertIsDisplayed().assertHasClickAction()
    composeTestRule
        .onNodeWithTag("IngredientOption")
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()
    composeTestRule
        .onNodeWithTag("DeleteIconButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()
    composeTestRule.onNodeWithTag("DeleteIconButton").assertIsNotDisplayed()
  }
}
