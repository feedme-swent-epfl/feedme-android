package com.android.feedme.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.screen.RecipeInputTestScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.RecipeInputScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeInputTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var recipeViewModel: RecipeViewModel

  @Test
  fun recipeInputTestDisplayed() {
    composeTestRule.setContent {
      RecipeInputScreen(
          mockk<NavigationActions>(relaxed = true), mockk<ProfileViewModel>(relaxed = true))
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<RecipeInputTestScreen>(composeTestRule) {
      recipePicture {
        assertIsDisplayed()
        assertHasClickAction()
      }

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
