package com.android.feedme.test.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
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

  }

  @After
  fun tearedDown() {
    // Clear the mocks
    unmockkAll()
  }

  @Test
  fun testIngredientDropDownMenuWorksAndHasClickableItem_Success() {
      val ingredient = Ingredient("Sugar", "sugarId", false, false)
      val expectedIngredientsList = listOf(ingredient)

      every { mockIngredientsRepository.getFilteredIngredients(any(), any(), any()) } answers
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
    unmockkAll()
  }

    @Test
    fun testIngredientDropDownMenuWorksAndHasClickableItem_Failure() {
        val ingredient = Ingredient("Sugar", "sugarId", false, false)
        val expectedIngredientsList = Exception()

        every { mockIngredientsRepository.getFilteredIngredients(any(), any(), any()) } answers
                {
                    val onFailure: (Exception) -> Unit = arg(2)
                    onFailure.invoke(expectedIngredientsList)
                }
        composeTestRule.setContent { IngredientList() }

        composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

        composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("IngredientsInput")
            .assertIsDisplayed()
            .performClick()
            .performTextInput("THIS TOO")

        unmockkAll()
    }

  @Test
  fun testIngredientListTestEverythingDisplayed() {
    composeTestRule.setContent { IngredientList() }

    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuantityInput").assertIsDisplayed().performTextInput("3")
    composeTestRule.onNodeWithTag("DoseBox").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("DoseInput").assertIsDisplayed()
      composeTestRule.onNodeWithText("tablespoon").assertIsDisplayed().assertHasClickAction()

  }
}
