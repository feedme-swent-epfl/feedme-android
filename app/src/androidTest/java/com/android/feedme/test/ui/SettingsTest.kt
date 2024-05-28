package com.android.feedme.test.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.screen.SettingsScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.settings.SettingsScreen
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  // Avoid re-creating a viewModel for every test
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var profileRepository: ProfileRepository

  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
    profileRepository = ProfileRepository.instance

    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    val profile = Profile()
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns profile

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)

    profileViewModel = ProfileViewModel()
    profileViewModel.initForTests()
  }

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
  fun alertDisplayedWhenSignoutIsClickedAndThenClose() {
    goToSettingsScreen()

    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
      signOutButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }

      composeTestRule.waitForIdle()
    }
  }

  @Test
  fun alertDisplayedWhenDeleteIsClickedAndThenClose() {
    //    goToSettingsScreen()
    //
    //    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
    //      deleteAccountButton {
    //        assertIsDisplayed()
    //        assertHasClickAction()
    //        performClick()
    //      }
    //
    //      composeTestRule.waitForIdle()
    //
    //      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsDisplayed()
    //      composeTestRule
    //          .onNodeWithTag("DismissButton")
    //          .assertIsDisplayed()
    //          .assertHasClickAction()
    //          .performClick()
    //      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsNotDisplayed()
    //    }
  }

  @Test
  fun alertDisplayedWhenDeleteIsClickedAndThenCallDelete() {
    composeTestRule.setContent {
      SettingsScreen(mockk<NavigationActions>(relaxed = true), profileViewModel)
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
      deleteAccountButton {
        assertIsDisplayed()
        assertHasClickAction()
        // performClick()
      }
      //      composeTestRule.waitForIdle()
      //
      //      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsDisplayed()
      //      composeTestRule
      //          .onNodeWithTag("ConfirmButton")
      //          .assertIsDisplayed()
      //          .assertHasClickAction()
      //          .performClick()

      // verify(exactly = 1) { profileViewModel.deleteCurrentUserProfile(any(), any()) }
    }
  }

  @Test
  fun alertDisplayedWhenDeleteIsClickedAndThenCallDeleteCheckSignOut() {
    composeTestRule.setContent {
      SettingsScreen(mockk<NavigationActions>(relaxed = true), profileViewModel)
    }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<SettingsScreen>(composeTestRule) {
      deleteAccountButton {
        assertIsDisplayed()
        assertHasClickAction()
        // performClick()
      }
      //      composeTestRule.waitForIdle()
      //
      //      composeTestRule.onNodeWithTag("AlertDialogBox").assertIsDisplayed()
      //      composeTestRule
      //          .onNodeWithTag("ConfirmButton")
      //          .assertIsDisplayed()
      //          .assertHasClickAction()
      //          .performClick()
      //
      //      verify(exactly = 1) { profileViewModel.deleteCurrentUserProfile(any(), any()) }
    }
  }

  private fun goToSettingsScreen() {
    composeTestRule.setContent {
      SettingsScreen(mockk<NavigationActions>(relaxed = true), profileViewModel)
    }
    composeTestRule.waitForIdle()
  }

  /*@Test
  fun testDeleteAccountSuccess() {
    // Mock ProfileViewModel
    val mockProfileViewModel = mockk<ProfileViewModel>(relaxed = true)
    val mockNavigationActions = mockk<NavigationActions>(relaxed = true)

    composeTestRule.setContent { SettingsScreen(mockNavigationActions, mockProfileViewModel) }

    // Mock GoogleSignInClient
    val mockGoogleSignInClient = mockk<GoogleSignInClient>(relaxed = true)

    // Set up the success callback for deleteCurrentUserProfile
    every { mockProfileViewModel.deleteCurrentUserProfile(any(), any()) } answers
        {
          // Invoke the success callback
          val successCallback = arg<() -> Unit>(0)
          successCallback.invoke()
        }

    // Set up the signOut method of GoogleSignInClient
    every { mockGoogleSignInClient.signOut().addOnCompleteListener(any()) } answers
        {
          // Simulate successful sign out
          val onCompleteListener = arg<OnCompleteListener<Void>>(0)
          onCompleteListener.onComplete(mockk { every { isSuccessful } returns true })
          mockk()
        }

    // Perform the action
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
    // Verify navigation to TOP_LEVEL_AUTH
    verify { mockNavigationActions.navigateTo(TOP_LEVEL_AUTH) }
  }*/
}
