package com.android.feedme.test.camera

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.screen.CameraScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Test
  fun buttonsAndCameraCorrectlyDisplayed() {
    goToCameraScreen()

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
    goToCameraScreen()

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
    goToCameraScreen()

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

  private fun goToCameraScreen() {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent { CameraScreen(navActions) }
    composeTestRule.waitForIdle()
  }
  // Test the normal case
  @Test
  fun MLButton() {
    goToCameraScreen()
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

      MLTextButton {
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("ML Text Box")
      composeTestRule.onNodeWithTag("ML Text Box inside")
    }
  }
  // Test the case where no photo was taken before asking for text recognition.
  @Test
  fun MLButtonWithNoPhoto() {
    goToCameraScreen()
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton { assertIsDisplayed() }

      MLTextButton {
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("ML Text Box")
      composeTestRule.onNodeWithTag("ML Text Box inside")
      composeTestRule.onNodeWithText("ERROR : no photo to analyse.").assertIsDisplayed()
    }
  }
}
