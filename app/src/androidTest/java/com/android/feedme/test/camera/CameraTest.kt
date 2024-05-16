package com.android.feedme.test.camera

import android.Manifest
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By.hasDescendant
import com.android.feedme.model.viewmodel.InputViewModel
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
    composeTestRule.setContent { CameraScreen(navActions, mockk<InputViewModel>()) }
    composeTestRule.waitForIdle()
  }

  // Test the normal case
  @Test
  fun MLTextButton() {
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

      mlTextButton {
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("ML Text Box").assertIsDisplayed()
      composeTestRule.onNodeWithTag("TopBarNavigation").assertIsDisplayed()
    }
  }

  // Test the case where no photo was taken before asking for text recognition.
  @Test
  fun MLTextButtonWithNoPhoto() {
    goToCameraScreen()
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton { assertIsDisplayed() }

      mlTextButton {
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("ML Text Box")
      composeTestRule.onNodeWithTag("ML Text Box inside")
      composeTestRule
          .onNodeWithText("ERROR : No photo to analyse, please take a picture.")
          .assertIsDisplayed()
    }
  }

  @Test
  fun MLBarcodeButton() {
    goToCameraScreen()
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      cameraPreview { assertIsDisplayed() }

      photoButton { assertIsDisplayed() }

      mlBarcodeButton {
        assertIsDisplayed()
        performClick()
      }

      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag("ML Text Box")
      composeTestRule.onNodeWithTag("ML Text Box inside")

      photoButton { performClick() }

      // Wait until the "Photo saved" text appears on the UI.
      composeTestRule.waitUntil(timeoutMillis = 5000) {
        composeTestRule.onNodeWithText("Photo saved", useUnmergedTree = true).isDisplayed()
      }

      mlBarcodeButton { performClick() }

      Thread.sleep(5000)

      composeTestRule.waitForIdle()
      // Find the node with the tag inside the Surface
      composeTestRule.onNodeWithTag("ML Text Box").assertIsDisplayed()
      Thread.sleep(5000)
      composeTestRule.onNodeWithTag("Column").assertIsDisplayed()
      composeTestRule.onNodeWithTag("Column").onChild().assertTextEquals("Cranberry protein - Farmer")

    }
  }
}
