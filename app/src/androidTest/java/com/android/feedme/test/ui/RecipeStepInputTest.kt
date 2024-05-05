package com.android.feedme.test.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.RecipeStepViewModel
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
  lateinit var recipeStepViewModel: RecipeStepViewModel

  private fun launchSListInputs() {
    recipeStepViewModel = RecipeStepViewModel()
    recipeStepViewModel.addStep(Step(1, "aled", "descc"))
    composeTestRule.setContent {
      StepList(Modifier.fillMaxWidth(), recipeStepViewModel = recipeStepViewModel)
    }
  }

  private fun launchStepInput() {
    composeTestRule.setContent { StepInput(Step(1, "aled", "descc"), {}, {}) }
  }

  @Test
  fun fullTest() {
    launchSListInputs()
    ComposeScreen.onComposeScreen<RecipeStepInputTestScreen>(composeTestRule) {
      titleList.assertIsDisplayed()
      expandButton.assertIsDisplayed()

      deleteButton.assertIsDisplayed()
      deleteButton.assertHasClickAction()
      deleteButton.performClick()
      assert(recipeStepViewModel.steps.value.isEmpty())

      addStepButton.assertIsDisplayed()
      addStepButton.assertHasClickAction()
      addStepButton.performClick()
      assert(recipeStepViewModel.steps.value.size == 1)

      titleField.performClick()
      titleField.performTextInput("Sample Title")

      descriptionField.performClick()
      descriptionField.performTextInput("Sample Description")
      assert(recipeStepViewModel.steps.value.first().stepNumber == 1)
      assert(recipeStepViewModel.steps.value.first().title == "Sample Title")
      assert(recipeStepViewModel.steps.value.first().description == "Sample Description")

      addStepButton.performClick()
      assert(recipeStepViewModel.steps.value.size == 2)

      recipeStepViewModel.resetSteps()
      assert(recipeStepViewModel.steps.value.isEmpty())
    }
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
    launchSListInputs()
    ComposeScreen.onComposeScreen<RecipeStepInputTestScreen>(composeTestRule) {
      expandButton.assertIsDisplayed()

      expandButton.performClick() // Expand to show the description field
      descriptionField.assertIsDisplayed()
      descriptionField.performClick()
      descriptionField.performTextClearance()

      descriptionError.assertIsDisplayed()
      descriptionError.assertTextEquals("Description cannot be empty.")

      descriptionField.performClick()
      descriptionField.performTextInput("Sample Description")

      composeTestRule.waitForIdle()
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
