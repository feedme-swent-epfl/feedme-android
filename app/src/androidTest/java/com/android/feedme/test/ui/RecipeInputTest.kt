package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.viewmodel.ProfileViewModel
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

  @Test
  fun recipeInputTestDisplayed() {
    composeTestRule.setContent {
      RecipeInputScreen(
          mockk<NavigationActions>(relaxed = true), mockk<ProfileViewModel>(relaxed = true))
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<RecipeInputTestScreen>(composeTestRule) {
      validateRecipe {
        assertIsDisplayed()
        assertHasClickAction()
      }

      recipePicture {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}
