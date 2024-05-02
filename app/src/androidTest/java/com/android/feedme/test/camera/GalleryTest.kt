package com.android.feedme.test.camera

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.screen.GalleryScreen
import com.android.feedme.ui.camera.GalleryScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // Grant gallery permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule =
      if (Build.VERSION.SDK_INT <= 32)
          GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)
      else GrantPermissionRule.grant(Manifest.permission.READ_MEDIA_IMAGES)

  @Test
  fun buttonsAndGalleryCorrectlyDisplayed() {
    goToGalleryScreen(10)

    /*ComposeScreen.onComposeScreen<GalleryScreen>(composeTestRule) {
      addPhotoButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
    }*/
  }

  private fun goToGalleryScreen(maxItems: Int) {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent { GalleryScreen(navActions, maxItems) }
    composeTestRule.waitForIdle()
  }
}
