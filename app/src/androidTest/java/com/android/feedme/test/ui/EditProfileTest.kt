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

  @Test
  fun testEditProfileInputsAndButtons() {
    goToEditProfileScreen()
    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {

      // Validate visibility and perform text clearance
      editPicture.assertIsDisplayed()
      nameInput.performTextClearance()
      nameInput.performTextInput("Jn")
      usernameInput.performTextClearance()
      usernameInput.performTextInput("J#123")
      bioInput.performTextClearance()
      bioInput.performTextInput("This is a sample bio repeated several times for testing")
      bioInput.performTextInput("This is a sample bio repeated several times for testing")

      // Check error messages
      nameError.assertTextEquals("Name must be at least 3 characters")
      usernameError.assertTextEquals("Username must be alphanumeric or underscores")
      bioError.assertTextEquals("Bio must be no more than 100 characters")

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

      // Ensuring Save button functionality

      saveButton.assertTextEquals("Save")
      composeTestRule.waitForIdle()
    }
  }

  @Test
  fun testSaveButton() {
    goToEditProfileScreen()
    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {
      saveButton.assertIsEnabled()
      saveButton.assertHasClickAction()
      composeTestRule.mainClock.autoAdvance = false

      saveButton.performClick()

      composeTestRule.mainClock.advanceTimeByFrame()
      composeTestRule.mainClock.advanceTimeByFrame()
      composeTestRule.mainClock.autoAdvance = true
      composeTestRule.waitForIdle()
    }
  }

  private fun goToEditProfileScreen() {
    composeTestRule.setContent { EditProfileScreen(mockNavActions) }
    composeTestRule.waitForIdle()
  }
}
