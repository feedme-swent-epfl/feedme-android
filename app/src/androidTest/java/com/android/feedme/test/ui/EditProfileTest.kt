package com.android.feedme.test.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.screen.EditProfileTestScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.EditProfileScreen
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
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
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavActions = mockk<NavigationActions>(relaxed = true)

  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var profileRepository: ProfileRepository? = null
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  @Before
  fun setupMocks() {
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    ProfileRepository.initialize(mockFirestore)

    profileRepository = ProfileRepository.instance

    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    val profile = Profile()
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns profile

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)
  }

  fun testIsDisplayed() {
    goToEditProfileScreen()
    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {
      editPicture.assertIsDisplayed()
      nameInput.assertIsDisplayed()
      usernameInput.assertIsDisplayed()
      bioInput.assertIsDisplayed()
      saveButton.assertIsDisplayed()
    }
  }

  @Test
  fun testIllegalModification() {
    goToEditProfileScreen()
    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {
      nameInput.performTextClearance()
      nameInput.performTextInput("Jn")
      usernameInput.performTextClearance()
      usernameInput.performTextInput("J#123")
      bioInput.performTextClearance()
      bioInput.performTextInput("This is a sample bio repeated several times for testing")
      bioInput.performTextInput("This is a sample bio repeated several times for testing")

      // Check error messages
      nameError.assertIsDisplayed()
      nameError.assertTextEquals("Name must be at least 3 characters")
      usernameError.assertIsDisplayed()
      usernameError.assertTextEquals("Username must be alphanumeric or underscores")
      bioError.assertIsDisplayed()
      bioError.assertTextEquals("Bio must be no more than 100 characters")
      saveButton.assertIsNotEnabled()

      // Clear inputs again to re-enter new values
      nameInput.performTextClearance()
      usernameInput.performTextClearance()
      bioInput.performTextClearance()

      usernameInput.performTextInput("johnjohnjohnjohnjohn")
      usernameError.assertTextEquals("Username must be no more than 15 characters")
      usernameInput.performTextClearance()

      // Re-entering text to test updated values
      nameInput.performTextInput("John")
      usernameInput.performTextInput("john")
      bioInput.performTextInput("This is a sample bio.")
      composeTestRule.waitForIdle()

      // Ensuring Save button functionality
      saveButton.assertIsDisplayed()
      saveButton.assertTextEquals("Save")
      saveButton.assertHasClickAction()
      saveButton.assertIsEnabled()
      composeTestRule.waitForIdle()
    }
  }

  @Test
  fun testSaveButton() {
    goToEditProfileScreen()
    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {
      nameInput.performTextClearance()
      usernameInput.performTextClearance()
      bioInput.performTextClearance()

      nameInput.performTextInput("John")
      usernameInput.performTextInput("john")
      bioInput.performTextInput("This is a sample bio.")
      composeTestRule.waitForIdle()

      saveButton {
        assertIsEnabled()
        assertTextEquals("Save")
        performClick()
      }
      composeTestRule.waitForIdle()
    }
  }

  @Test
  fun testProfilePicture() {
    goToEditProfileScreen()
    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {
      editPicture.assertIsDisplayed()
      editPicture.assertHasClickAction()
      editPicture.performClick()
    }
  }

  private fun goToEditProfileScreen() {
    composeTestRule.setContent { EditProfileScreen(mockNavActions) }
    composeTestRule.waitForIdle()
  }
}
