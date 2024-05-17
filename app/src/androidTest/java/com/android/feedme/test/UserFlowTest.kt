package com.android.feedme.test

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.MainActivity
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.screen.CameraScreen
import com.android.feedme.screen.FindRecipeScreen
import com.android.feedme.screen.LandingScreen
import com.android.feedme.screen.LoginScreen
import com.android.feedme.test.auth.mockGoogleSignInAccount
import com.android.feedme.ui.auth.setLoginMockingForTests
import com.android.feedme.ui.auth.setTestMode
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserFlowTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

  // Grant gallery permission for tests
  @get:Rule
  val permissionRuleGallery: GrantPermissionRule =
      if (Build.VERSION.SDK_INT <= 32)
          GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE)
      else GrantPermissionRule.grant(android.Manifest.permission.READ_MEDIA_IMAGES)

  @Before
  fun setup() {
    // Set up the test environment for Login mocking
    setTestMode(true)
    setLoginMockingForTests(::mockGoogleSignInAccount)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }
    ProfileRepository.initialize(mockFirestore)
  }

  @After
  fun teardown() {
    // Reset the test environment after the test
    setTestMode(false)
  }

  @Test
  fun userFlowFromLoginPageThroughAllTopLevelDestinations() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Click on the "Sign in with Google" button
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the login to complete
      composeTestRule.waitForIdle()
    }

    // From HOME Page go to SAVED page
    composeTestRule.onNodeWithContentDescription("Saved").assertIsDisplayed().performClick()

    // Wait for the EXPLORE page to load
    composeTestRule.waitForIdle()

    // From EXPLORE Page go to FIND_RECIPE page
    composeTestRule.onNodeWithContentDescription("Find Recipe").assertIsDisplayed().performClick()

    // Wait for the FIND_RECIPE page to load
    composeTestRule.waitForIdle()

    // From FIND_RECIPE Page go to PROFILE page
    // TODO We got to Mockk Firebase another issue
    // composeTestRule.onNodeWithContentDescription("Profile").assertIsDisplayed().performClick()

    // Wait for the PROFILE page to load
    composeTestRule.waitForIdle()

    // From PROFILE Page go to SETTINGS page
    composeTestRule.onNodeWithContentDescription("Settings").assertIsDisplayed().performClick()

    // Wait for the SETTINGS page to load
    composeTestRule.waitForIdle()

    // From SETTINGS Page go to HOME page
    composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed().performClick()

    // Wait for the HOME page to load
    composeTestRule.waitForIdle()
  }

  @Test
  fun userFlowFromLoginPageToCameraAndTakePhoto() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Click on the "Sign in with Google" button
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the login to complete
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      // From Home Page go to FIND_RECIPE page
      composeTestRule.onNodeWithContentDescription("Find Recipe").assertIsDisplayed().performClick()

      // Wait for the FIND_RECIPE page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<FindRecipeScreen>(composeTestRule) {
      cameraButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the CAMERA page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait until the "Photo saved" text appears on the UI.
      composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule.onNodeWithText("Photo saved", useUnmergedTree = true).isDisplayed()
      }

      // Click on the gallery button
      galleryButton {
        assertIsDisplayed()
        performClick()
      }

      //      // Wait for the gallery to be displayed
      //      composeTestRule.waitForIdle()
      //
      //      // Assert that the photos are displayed after clicking
      //      composeTestRule
      //          .onNodeWithContentDescription("Photo", useUnmergedTree = true)
      //          .assertIsDisplayed()
    }
  }

  /*
  // TODO We got to Mockk Firebase another issue
  @Test
  fun userFlowFromLoginPageToProfileAndNavigateThroughSubScreens() {
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Click on the "Sign in with Google" button
      loginButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the login to complete
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      // From Home Page go to PROFILE page
      composeTestRule.onNodeWithText("Profile").assertIsDisplayed().performClick()

      // Wait for the PROFILE page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      followerDisplayButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the FRIENDS page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<FriendsScreen>(composeTestRule) {
      tabFollowing {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the FOLLOWING list to load
      composeTestRule.waitForIdle()

      followingList { assertIsDisplayed() }

      tabFollowers {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the FOLLOWERS list to load
      composeTestRule.waitForIdle()

      followersList { assertIsDisplayed() }

      composeTestRule
          .onNodeWithTag("LeftIconButton")
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()

      // Wait for the PROFILE page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<ProfileScreen>(composeTestRule) {
      editButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the EDIT PROFILE page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<EditProfileTestScreen>(composeTestRule) {
      // Wait for the EDIT PROFILE page to load
      composeTestRule.waitForIdle()

      composeTestRule
          .onNodeWithTag("LeftIconButton")
          .assertIsDisplayed()
          .assertHasClickAction()
          .performClick()
    }
  }
   */
}
