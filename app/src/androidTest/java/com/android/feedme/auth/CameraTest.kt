package com.android.feedme.auth

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.MainActivity
import com.android.feedme.screen.CameraScreen
import com.android.feedme.ui.camera.CameraScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Test
  fun buttonsAreCorrectlyDisplayed() {
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
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
    // The initial state of your ViewModel is an empty gallery
    composeTestRule.setContent { CameraScreen() }

    // Interact with the CameraScreen
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      photoButton {
        assertIsDisplayed()
      }

      galleryButton {
        assertIsDisplayed()
        performClick()
      }

      emptyGalleryText {
        assertIsDisplayed()
        assertTextEquals("There are no photos yet")
      }
    }
  }

  @Test
  fun galleryButtonDisplayGalleryAfterTakingPhoto() {
    // Provide the ViewModel with the dummy photo to the CameraScreen.
    composeTestRule.setContent { CameraScreen() }

    // Interact with the CameraScreen.
    ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
      photoButton {
        assertIsDisplayed()
        performClick()
      }

      galleryButton {
        assertIsDisplayed()
        performClick()
      }

      // Assert that the photos are displayed
      photos {
        assertIsDisplayed()
      }
    }
  }
}
