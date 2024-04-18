package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.ProfileScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.ProfileScreen
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun profileBoxAndComponentsCorrectlyDisplayed() {
    goToProfileScreen()
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      topBarProfile { assertIsDisplayed() }

      bottomBarProfile { assertIsDisplayed() }

      profileBox { assertIsDisplayed() }

      profileName { assertIsDisplayed() }

      profileIcon { assertIsDisplayed() }

      profileBio { assertIsDisplayed() }

      followerButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      followingButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      editButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      shareButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  private fun goToProfileScreen() {
    composeTestRule.setContent { ProfileScreen((mockk<NavigationActions>())) }
    composeTestRule.waitForIdle()
  }
}
