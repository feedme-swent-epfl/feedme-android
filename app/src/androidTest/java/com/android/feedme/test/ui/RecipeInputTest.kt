package com.android.feedme.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeStepViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.screen.RecipeInputTestScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.RecipeInputScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeInputTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val navigationActions: NavigationActions = mockk(relaxed = true)

  private val profileViewModel: ProfileViewModel = mockk(relaxed = true)
  private val recipeViewModel: RecipeViewModel = mockk(relaxed = true)
  private val inputViewModel: InputViewModel = mockk(relaxed = true)
  private val recipeStepViewModel: RecipeStepViewModel = mockk(relaxed = true)

  @Before
  fun setUp() {
    every { recipeViewModel.errorMessageVisible } returns MutableStateFlow(false)
  }

  @Test
  fun recipeInputTestDisplayedAndValidates() {
    composeTestRule.setContent {
      RecipeInputScreen(
          navigationActions, profileViewModel, recipeStepViewModel, inputViewModel, recipeViewModel)
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<RecipeInputTestScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      bottomBar { assertIsDisplayed() }
      recipeInputBox { assertIsDisplayed() }
      recipePicture { assertIsDisplayed() }

      validateRecipe {
        assertIsDisplayed()
        assertHasClickAction()
      }

      titleInput {
        assertIsDisplayed()
        // TODO add input
      }
      ingredientsInput { assertIsDisplayed() }
      stepsInput { assertIsDisplayed() }

      validateRecipe {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun recipeViewModelValidateFails() {
    //    composeTestRule.setContent {
    //      RecipeInputScreen(
    //          mockk<NavigationActions>(relaxed = true), mockk<ProfileViewModel>(relaxed = true))
    //    }
    //    composeTestRule.waitForIdle()
    //
    //    ComposeScreen.onComposeScreen<RecipeInputTestScreen>(composeTestRule) {
    //      validateRecipe {
    //        assertIsDisplayed()
    //        performClick()
    //      }
    //      composeTestRule.onNodeWithTag("Error Snack Bar").assertIsDisplayed()
    //      composeTestRule
    //          .onNodeWithTag("Error Snack Bar")
    //          .onChild()
    //          .assertTextEquals("Error : Recipe not correctly filled in")
    //      composeTestRule.waitForIdle()
    //
    //      composeTestRule.waitUntil(timeoutMillis = 25000) {
    //        composeTestRule.onNodeWithTag("Error Snack Bar").isNotDisplayed()
    //      }
  }
}
