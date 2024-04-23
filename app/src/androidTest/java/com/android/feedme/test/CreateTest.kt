package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.CreateScreen
import com.android.feedme.ui.CreateScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateTest : TestCase() {
  // @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun mainComponentsAreDisplayed() {
    goToCreateScreen()

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
      topBarLanding { assertIsDisplayed() }

      bottomBarLanding { assertIsDisplayed() }

      cameraButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  private fun goToCreateScreen() {
    composeTestRule.setContent { CreateScreen(mockk<NavigationActions>()) }
    composeTestRule.waitForIdle()
  }
}
