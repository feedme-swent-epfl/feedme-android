package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.screen.ProfileScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.ProfileScreen
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val navAction = mockk<NavigationActions>(relaxed = true)
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
    RecipeRepository.initialize(mockFirestore)
    profileViewModel = ProfileViewModel()
    profileViewModel.updateCurrentUserProfile(Profile())
  }

  @Test
  fun profileBoxAndComponentsCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      composeTestRule.setContent { ProfileScreen(navAction, profileViewModel, emptyList()) }

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
      recipeSmall { assertIsDisplayed() }
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
      profileViewModel.setViewingProfile(Profile(id = "ID_DEFAULT_1"))
      composeTestRule.setContent { ProfileScreen(navAction, profileViewModel, emptyList()) }

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
      }
    }
  }
}
