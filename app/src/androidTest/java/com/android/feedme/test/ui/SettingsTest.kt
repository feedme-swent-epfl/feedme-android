package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import com.android.feedme.screen.SettingsScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.settings.SettingsScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class SettingsTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun settingsScreenDisplayed() {
    goToSettingsScreen()

    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
      signOutButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      displayBox { assertIsDisplayed() }

      deleteAccountButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  private fun goToSettingsScreen() {
    composeTestRule.setContent { SettingsScreen(mockk<NavigationActions>(relaxed = true)) }
    composeTestRule.waitForIdle()
  }
}
