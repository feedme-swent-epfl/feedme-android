package com.android.feedme.test.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.profile.EditProfileScreen
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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
    composeTestRule.setContent { EditProfileScreen(mockNavActions) }
    composeTestRule.waitForIdle()

    // Assuming that initial values are set for simplicity; adjust as per your setup.
    composeTestRule.onNodeWithText("Edit Picture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("NameInput").performTextClearance()
    composeTestRule.onNodeWithTag("NameInput").assertIsDisplayed().performTextInput("Jn")
    composeTestRule.onNodeWithTag("UsernameInput").performTextClearance()
    composeTestRule.onNodeWithTag("UsernameInput").assertIsDisplayed().performTextInput("J#123")
    composeTestRule.onNodeWithTag("BioInput").performTextClearance()
    composeTestRule
        .onNodeWithTag("BioInput")
        .assertIsDisplayed()
        .performTextInput("This is a sample bio.")

    // Check if error messages appear for each field when needed
    composeTestRule
        .onNodeWithTag("NameError")
        .assertTextEquals("Name must be at least 3 characters")
    composeTestRule
        .onNodeWithTag("UsernameError")
        .assertTextEquals("Username must be alphanumeric or underscores")

    composeTestRule
        .onNodeWithTag("BioError")
        .assertDoesNotExist() // No error expected with correct input

    composeTestRule.onNodeWithTag("NameInput").performTextClearance()
    composeTestRule.onNodeWithTag("UsernameInput").performTextClearance()
    composeTestRule.onNodeWithTag("BioInput").performTextClearance()

    composeTestRule.onNodeWithTag("NameInput").assertIsDisplayed().performTextInput("John")
    composeTestRule.onNodeWithTag("UsernameInput").assertIsDisplayed().performTextInput("john")
    composeTestRule
        .onNodeWithTag("BioInput")
        .assertIsDisplayed()
        .performTextInput("This is a sample bio.")

    // Ensure Save button is clickable and performs the expected action
    composeTestRule.onNodeWithText("Save").assertIsEnabled().assertHasClickAction()
    composeTestRule.onNodeWithText("Save").performClick()
  }
}
