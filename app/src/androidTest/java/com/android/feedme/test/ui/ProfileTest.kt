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
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
  private val navAction = mockk<NavigationActions>(relaxed = true)

  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
    RecipeRepository.initialize(mockFirestore)
    profileViewModel = ProfileViewModel()
    profileViewModel.updateCurrentUserProfile(Profile())

    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns
        Profile(id = "ID_DEFAULT_1")

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)
  }

  @Test
  fun profileBoxAndComponentsCorrectlyDisplayed() {
    composeTestRule.setContent { ProfileScreen(navAction, profileViewModel) }
    composeTestRule.waitForIdle()

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
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

      tabRow { assertIsDisplayed() }

      recipeSmall { assertIsDisplayed() }

      editButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      addRecipe {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun viewingDisplayedProfileIsCorrectAndFollowButtonWorks() {
    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      profileViewModel.setViewingProfile(Profile(id = "ID_DEFAULT_1"))
      composeTestRule.setContent { ProfileScreen(navAction, profileViewModel) }

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

      followerButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}
