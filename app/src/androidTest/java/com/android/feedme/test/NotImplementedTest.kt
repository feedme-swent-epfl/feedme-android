package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.NotImplementedScreen
import com.android.feedme.ui.NotImplementedScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotImplementedTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun mainComponentsAreDisplayed() {
    goToCreateScreen()

    ComposeScreen.onComposeScreen<NotImplementedScreen>(composeTestRule) {
      topBarLanding { assertIsDisplayed() }

      bottomBarLanding { assertIsDisplayed() }

      middleText { assertIsDisplayed() }
    }
  }

  private fun goToCreateScreen() {
    composeTestRule.setContent { NotImplementedScreen(mockk<NavigationActions>(), Route.SETTINGS) }
    composeTestRule.waitForIdle()
  }
}
