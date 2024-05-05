package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Step
import com.android.feedme.screen.RecipeStepInputTestScreen
import com.android.feedme.ui.component.StepInput
import com.android.feedme.ui.component.StepList
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeStepInputTest {
  @get:Rule val composeTestRule = createComposeRule()

  private fun launchSListInputs() {
    composeTestRule.setContent { StepList() }
  }

  private fun launchStepInput() {
    composeTestRule.setContent { StepInput(Step(1, "aled", "descc"), {}, {}) }
  }

  @Test
  fun testStepInput_isDisplayedCorrectly() {
    launchSListInputs()
    ComposeScreen.onComposeScreen<RecipeStepInputTestScreen>(composeTestRule) {
      titleField.assertIsDisplayed()
      expandButton.assertIsDisplayed()
      deleteButton.assertIsDisplayed()
    }
  }

  @Test
  fun testTitleFieldValidation_WhenEmpty_ShowsError() {
    launchSListInputs()
    ComposeScreen.onComposeScreen<RecipeStepInputTestScreen>(composeTestRule) {
      titleField.performClick() // Focus the title field
      titleField.performTextInput("") // Clear any text if present
      titleField.performTextClearance() // Ensuring the field is empty

      expandButton.performClick() // May need to interact with the field to trigger validation

      titleError.assertIsDisplayed()
      titleError.assertTextEquals("Title cannot be empty.")
    }
  }

  @Test
  fun testDescriptionField_ExpandAndValidate() {
    launchStepInput()
    ComposeScreen.onComposeScreen<RecipeStepInputTestScreen>(composeTestRule) {
      expandButton.performClick() // Expand to show the description field
      descriptionField.performTextClearance()
      descriptionField.performTextInput("Sample Description")
      composeTestRule.waitForIdle()
      descriptionField.performTextClearance()
      descriptionField.performTextInput("")
      composeTestRule.waitForIdle()

      descriptionError.assertIsDisplayed()
      descriptionError.assertTextEquals("Description cannot be empty.")
    }
  }

  @Test
  fun testDeleteAction_ShouldRemoveStep() {
    launchSListInputs()
    ComposeScreen.onComposeScreen<RecipeStepInputTestScreen>(composeTestRule) {
      deleteButton.performClick()
    }
  }
}
