package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.FriendsScreen
import com.android.feedme.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendsTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun tabsAndComponentsCorrectlyDisplayed() {
    goToFriendsScreen()
    ComposeScreen.onComposeScreen<FriendsScreen>(composeTestRule) {
      tabFollowers {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      followersList { assertIsDisplayed() }
      followerCard { assertIsDisplayed() }

      tabFollowing {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      followingList.assertIsDisplayed()

      followerCard { assertIsDisplayed() }
    }
  }

  private fun goToFriendsScreen() {
    val mockNavActions = mockk<NavigationActions>(relaxed = true) // Make the mock relaxed
    composeTestRule.setContent {
      // Assuming there's a mocked navigation action and a default state for the lists
      com.android.feedme.ui.profile.FriendsScreen(mockNavActions, mode = 0)
    }
    composeTestRule.waitForIdle()
  }
}
