package com.android.feedme.test.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.ui.component.IngredientList
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IngredientListTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockIngredientsRepository = mockk<IngredientsRepository>()

  @Before
  fun setup() {
    unmockkAll()
    mockkObject(IngredientsRepository)
    mockkObject(IngredientsRepository.Companion)
    IngredientsRepository.initialize(mockIngredientsRepository)

    val ingredient = Ingredient("Sugar", "sugarId", false, false)
    val expectedIngredientsList = listOf(ingredient)
    IngredientsRepository.initialize(mockIngredientsRepository)
    every { mockIngredientsRepository.getFilteredIngredients(any(), any(), any()) } answers
        {
          val onSuccess: (List<Ingredient>) -> Unit = arg(1)
          val onFailure: (Exception) -> Unit = arg(2)
          onFailure.invoke(Exception())
          onSuccess.invoke(expectedIngredientsList)
        }
    every { mockIngredientsRepository.addIngredient(any(), any(), any()) } answers
        {
          val onSuccess: (Ingredient) -> Unit = arg(1)
          val onFailure: (Exception) -> Unit = arg(2)
          onFailure.invoke(Exception())
          onSuccess.invoke(ingredient)
        }
    composeTestRule.setContent { IngredientList() }
  }

  @After
  fun tearedDown() {
    // Clear the mocks
    unmockkAll()
  }

  @Test
  fun testIngredientDropDownMenuWorksAndHasClickableItem_Success() {
    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("IngredientsInput")
        .assertIsDisplayed()
        .performClick()
        .performTextInput("DONT CHANGE")
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
    composeTestRule.onNodeWithTag("QuantityInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoseBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoseInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IngredientDivider").assertIsDisplayed()
  }

  @Test
  fun testIngredientDropDownMenuWorksAndHasClickableItem_Failure() {
    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("IngredientsInput")
        .assertIsDisplayed()
        .performClick()
        .performTextInput("THIS TOO")
  }

  @Test
  fun testIngredientListTestEverythingDisplayed() {
    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuantityInput").assertIsDisplayed().performTextInput("3")
    composeTestRule.onNodeWithTag("DoseBox").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("DoseInput").assertIsDisplayed()
  }

  @Test
  fun testIngredientDropDownMenuWorksAndAddIngredient_Success() {
    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("IngredientsInput")
        .assertIsDisplayed()
        .performClick()
        .performTextInput("DONT CHANGE")
    composeTestRule
        .onNodeWithTag("AddOption")
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()
  }

  @Test
  fun completeIngredientsCanBeChecked() {
    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IngredientsInput").assertIsDisplayed().performTextInput("Item 1")

    composeTestRule.onNodeWithText("Sugar").performClick()

    composeTestRule.onAllNodesWithTag("QuantityInput")[0].assertIsDisplayed().performTextInput("2")
    composeTestRule.onAllNodesWithTag("DoseInput")[0].assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("teaspoon").assertIsDisplayed().performClick()

    composeTestRule
        .onNodeWithTag("CheckIconButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("IngredientName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Quantity&Dose").assertIsDisplayed()

    composeTestRule.onNodeWithTag("DeleteIconButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ModifyIconButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()
  }
}
