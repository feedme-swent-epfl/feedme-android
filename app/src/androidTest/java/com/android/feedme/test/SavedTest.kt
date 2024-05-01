package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.SavedScreen
import com.android.feedme.ui.SavedScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SavedTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun mainComponentsAreDisplayed() {
    goToSavedScreen()

    ComposeScreen.onComposeScreen<SavedScreen>(composeTestRule) {
      topBarLanding { assertIsDisplayed() }

      bottomBarLanding { assertIsDisplayed() }

      middleText { assertIsDisplayed() }
    }
  }

  private fun goToSavedScreen() {
    composeTestRule.setContent { SavedScreen(mockk<NavigationActions>(), Route.SETTINGS) }
    composeTestRule.waitForIdle()
  }
}
