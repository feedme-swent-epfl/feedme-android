package com.android.feedme.test.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.screen.WelcomeScreen
import com.android.feedme.ui.auth.WelcomeScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WelcomePageTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val navAction = mockk<NavigationActions>(relaxed = true)
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
    profileViewModel = ProfileViewModel()
    profileViewModel.updateCurrentUserProfile(Profile())
  }

  @Test
  fun screenIsCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
      composeTestRule.setContent { WelcomeScreen(navAction, profileViewModel) }

      composeTestRule.onNodeWithTag("MainBox").assertIsDisplayed()
      composeTestRule.onNodeWithTag("InnerBox").assertIsDisplayed()
      composeTestRule.onNodeWithTag("InnerColumn").assertIsDisplayed()
      composeTestRule.onNodeWithTag("WelcomeText").assertIsDisplayed()
      composeTestRule.onNodeWithTag("NoAccText").assertIsDisplayed()
      composeTestRule.onNodeWithTag("OutlinedButton").assertIsDisplayed().assertHasClickAction()
      composeTestRule.onNodeWithTag("LogoImage").assertIsDisplayed()
    }
  }
}
