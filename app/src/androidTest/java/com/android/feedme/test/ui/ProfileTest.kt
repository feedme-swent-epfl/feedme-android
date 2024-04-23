package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.screen.ProfileScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.ProfileScreen
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
  }

  @Test
  fun profileBoxAndComponentsCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      val mockProfileViewModel = mockk<ProfileViewModel>()
      val profile =
          Profile(
              // Sample profile data
              name = "John Doe",
              username = "johndoe",
              followers = listOf("follower1", "follower2"),
              following = listOf("following1", "following2"),
              description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
              // Add any other required fields for Profile
              )

      every { mockProfileViewModel.currentUserId } returns "ID_DEFAULT"
      every { mockProfileViewModel.viewingUserId } returns null
      every { mockProfileViewModel.isViewingProfile() } returns false
      every { mockProfileViewModel.profileToShow() } returns profile
      every { mockProfileViewModel.fetchCurrentUserProfile() } returns Unit

      composeTestRule.setContent { ProfileScreen(mockk<NavigationActions>(), mockProfileViewModel) }

      topBarProfile { assertIsDisplayed() }

      bottomBarProfile { assertIsDisplayed() }

      profileBox { assertIsDisplayed() }

      profileName { assertIsDisplayed() }

      profileIcon { assertIsDisplayed() }

      profileBio { assertIsDisplayed() }

      followerDisplayButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      followingDisplayButton {
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

  @Test
  fun viewingDisplayedProfileIsCorrectAndFollowButtonWorks() {
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      val mockProfileViewModel = mockk<ProfileViewModel>()
      val profile =
          Profile(
              // Sample profile data
              name = "John Doe",
              username = "johndoe",
              followers = listOf("follower1", "follower2"),
              following = listOf("following1", "following2"),
              description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
              // Add any other required fields for Profile
              )

      every { mockProfileViewModel.currentUserId } returns "ID_DEFAULT_1"
      every { mockProfileViewModel.viewingUserId } returns "ID_DEFAULT_2"
      every { mockProfileViewModel.isViewingProfile() } returns true
      every { mockProfileViewModel.profileToShow() } returns profile
      every { mockProfileViewModel.fetchCurrentUserProfile() } returns Unit

      composeTestRule.setContent { ProfileScreen(mockk<NavigationActions>(), mockProfileViewModel) }

      topBarProfile { assertIsDisplayed() }

      bottomBarProfile { assertIsDisplayed() }

      profileBox { assertIsDisplayed() }

      profileName { assertIsDisplayed() }

      profileIcon { assertIsDisplayed() }

      profileBio { assertIsDisplayed() }

      followerDisplayButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      followingDisplayButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      editButton { assertIsNotDisplayed() }

      shareButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      followerButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }

      followingButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}
