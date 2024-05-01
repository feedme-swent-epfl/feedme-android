package com.android.feedme.test.camera

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.screen.GalleryScreen
import com.android.feedme.ui.camera.GalleryScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)

  @Test
  fun buttonsAndGalleryCorrectlyDisplayed() {
    goToGalleryScreen(10)

    ComposeScreen.onComposeScreen<GalleryScreen>(composeTestRule) {
      galleryButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun galleryButtonIsClickable() {
    goToGalleryScreen(10)

    ComposeScreen.onComposeScreen<GalleryScreen>(composeTestRule) {
      galleryButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  private fun goToGalleryScreen(maxItems: Int) {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent { GalleryScreen(navActions, maxItems) }
    composeTestRule.waitForIdle()
  }
}
