package com.android.feedme.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.MainActivity
import com.android.feedme.screen.CameraScreen
import com.android.feedme.screen.CreateScreen
import com.android.feedme.screen.LandingScreen
import com.android.feedme.screen.LoginScreen
import com.android.feedme.test.auth.mockGoogleSignInAccount
import com.android.feedme.ui.CreateScreen
import com.android.feedme.ui.auth.setLoginMockingForTests
import com.android.feedme.ui.auth.setTestMode
import com.android.feedme.ui.camera.CameraScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserFlowTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

  @Before
  fun setup() {
    // Set up the test environment for Login mocking
    setTestMode(true)
    setLoginMockingForTests(::mockGoogleSignInAccount)
  }

  @After
  fun teardown() {
    // Reset the test environment after the test
    setTestMode(false)
  }

  @Test
  fun userFlowFromHomePageNavigateToCameraAndTakePhoto() {
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
      // From Home Page go to CREATE page
      composeTestRule.onNodeWithText("Create").assertIsDisplayed().performClick()

      // Wait for the CREATE page to load
      composeTestRule.waitForIdle()
    }

    ComposeScreen.onComposeScreen<CreateScreen>(composeTestRule) {
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

      // Wait for the gallery to be displayed
      composeTestRule.waitForIdle()

      // Assert that the photos are displayed after clicking
      composeTestRule
          .onNodeWithContentDescription("Photo", useUnmergedTree = true)
          .assertIsDisplayed()
    }
  }
}
