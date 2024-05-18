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
import com.android.feedme.ui.component.IngredientList
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IngredientListTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testIngredientListTestEverythingDisplayed() {
    composeTestRule.setContent { IngredientList() }

    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("QuantityInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoseBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoseInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IngredientDivider").assertIsDisplayed()
  }

  @Test
  fun testIngredientDropDownMenuWorksAndHasClickableItem() {

    composeTestRule.setContent { IngredientList() }

    composeTestRule.onNodeWithTag("LazyList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("IngredientsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IngredientsInput").assertIsDisplayed().performTextInput("Item 1")
    composeTestRule
        .onNodeWithText("Item 1")
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
