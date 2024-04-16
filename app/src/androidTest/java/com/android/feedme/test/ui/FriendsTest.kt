package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.screen.FriendsScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendsTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavActions = mockk<NavigationActions>(relaxed = true)
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  @Before
  fun setupMocks() {
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    ProfileRepository.initialize(mockFirestore)
  }

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
      com.android.feedme.ui.profile.FriendsScreen(mockNavActions, mode = 0)
    }
    composeTestRule.waitForIdle()
  }
}
