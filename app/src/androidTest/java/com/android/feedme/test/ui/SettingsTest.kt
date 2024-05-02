package com.android.feedme.test.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.screen.SettingsScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.settings.SettingsScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import io.mockk.verify
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

      deleteAccountButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun alertDisplayedWhenDeleteIsClickedAndThenClose() {
    goToSettingsScreen()

    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
      deleteAccountButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }

      composeTestRule.waitForIdle()

      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("DismissButton")
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()
      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsNotDisplayed()
    }
  }

  @Test
  fun alertDisplayedWhenDeleteIsClickedAndThenCallDelete() {
    val mockProfileViewModel = mockk<ProfileViewModel>(relaxed = true)
    composeTestRule.setContent {
      SettingsScreen(mockk<NavigationActions>(relaxed = true), mockProfileViewModel)
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
      deleteAccountButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      composeTestRule.waitForIdle()

      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("ConfirmButton")
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()

      verify(exactly = 1) { mockProfileViewModel.deleteCurrentUserProfile(any(), any()) }
    }
  }

  private fun goToSettingsScreen() {
    composeTestRule.setContent {
      SettingsScreen(
          mockk<NavigationActions>(relaxed = true), mockk<ProfileViewModel>(relaxed = true))
    }
    composeTestRule.waitForIdle()
  }
}
