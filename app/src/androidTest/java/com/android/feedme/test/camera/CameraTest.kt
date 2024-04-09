package com.android.feedme.test.camera

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.CurrentScreen
import com.android.feedme.MainActivity
import com.android.feedme.screen.CameraScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Before
  fun setup() {
    val scenario = ActivityScenario.launch(MainActivity::class.java)
    scenario.onActivity { activity ->
      activity.setScreen(CurrentScreen.CAMERA) // For CameraTest
    }
  }

  @Test
  fun buttonsAndCameraCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      galleryButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun galleryButtonDisplayGalleryWhenEmpty() {
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton { assertIsDisplayed() }

      galleryButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait for the gallery to be displayed
      composeTestRule.waitForIdle()

      // Assert that the empty gallery text is displayed after clicking
      composeTestRule
          .onNodeWithText("There are no photos yet", useUnmergedTree = true)
          .assertIsDisplayed()
    }
  }

  @Test
  fun galleryButtonDisplayGalleryAfterTakingPhoto() {
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton {
        assertIsDisplayed()
        performClick()
      }

      // Wait until the "Photo saved" text appears on the UI.
      composeTestRule.waitUntil(timeoutMillis = 5000) {
        try {
          composeTestRule.onNodeWithText("Photo saved", useUnmergedTree = true).assertExists()
          true // If assertExists() succeeds, return true to end waitUntil.
        } catch (e: AssertionError) {
          false // If the node isn't found, return false so waitUntil keeps trying.
        }
      }

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
