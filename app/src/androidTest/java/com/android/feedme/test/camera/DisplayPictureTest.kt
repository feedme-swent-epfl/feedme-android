package com.android.feedme.test.camera

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.screen.CameraScreen
import com.android.feedme.screen.DisplayPictureScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.camera.DisplayPicture
import com.android.feedme.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayPictureTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  // Grant gallery permission for tests
  @get:Rule
  val permissionRuleGallery: GrantPermissionRule =
      if (Build.VERSION.SDK_INT <= 32)
          GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE)
      else GrantPermissionRule.grant(android.Manifest.permission.READ_MEDIA_IMAGES)

  private lateinit var cameraViewModel: CameraViewModel

  @Before
  fun init() {
    cameraViewModel = CameraViewModel()
  }

  @Test
  fun buttonsCorrectlyDisplayed() {
    gotoDisplayPicture()

    ComposeScreen.onComposeScreen<DisplayPictureScreen>(composeTestRule) {
      mlBarcodeButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      mlTextButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  private fun goToCameraScreen() {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent {
      CameraScreen(navActions, mockk<InputViewModel>(), cameraViewModel)
    }
    composeTestRule.waitForIdle()
  }

  private fun gotoDisplayPicture() {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent {
      DisplayPicture(navActions, mockk<InputViewModel>(), cameraViewModel)
    }
    composeTestRule.waitForIdle()
  }

  // Test the normal case
  //    @Test
  //    fun MLTextButton() {
  //        gotoDisplayPicture()
  //        ComposeScreen.onComposeScreen<DisplayPictureScreen>(composeTestRule) {
  //
  //            mlTextButton {
  //                assertIsDisplayed()
  //                performClick()
  //            }
  //        }
  //        goToCameraScreen()
  //        ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
  //
  //            composeTestRule.onNodeWithTag("Error Snack Bar").assertIsDisplayed()
  //            composeTestRule
  //                .onNodeWithTag("Error Snack Bar")
  //                .onChild()
  //                .assertTextEquals("ERROR : No photo to analyse, please take a picture.")
  //            composeTestRule.waitForIdle()
  //
  //            composeTestRule.waitUntil(timeoutMillis = 25000) {
  //                composeTestRule.onNodeWithTag("Error Snack Bar").isNotDisplayed()
  //            }
  //
  //            photoButton{
  //                assertIsDisplayed()
  //                performClick()
  //            }
  //        }
  //
  //        gotoDisplayPicture()
  //        ComposeScreen.onComposeScreen<DisplayPictureScreen>(composeTestRule) {
  //
  //            mlTextButton {
  //                assertIsDisplayed()
  //                performClick()
  //            }
  //        }
  //        goToCameraScreen()
  //
  //
  //        ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
  //
  //            composeTestRule.onNodeWithTag("Error Snack Bar").assertIsDisplayed()
  //            composeTestRule
  //                .onNodeWithTag("Error Snack Bar")
  //                .onChild()
  //                .assertTextEquals("Failed to identify text, please try again.")
  //            composeTestRule.waitForIdle()
  //
  //            composeTestRule.waitUntil(timeoutMillis = 25000) {
  //                composeTestRule.onNodeWithTag("Error Snack Bar").isNotDisplayed()
  //            }
  //
  //        }
  //    }
  //
  //    @Test
  //    fun MLBarcodeButton() {
  //        gotoDisplayPicture()
  //        ComposeScreen.onComposeScreen<DisplayPictureScreen>(composeTestRule) {
  //
  //            mlBarcodeButton {
  //                assertIsDisplayed()
  //                performClick()
  //            }
  //        }
  //        goToCameraScreen()
  //        ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
  //
  //            composeTestRule.onNodeWithTag("Error Snack Bar").assertIsDisplayed()
  //            composeTestRule
  //                .onNodeWithTag("Error Snack Bar")
  //                .onChild()
  //                .assertTextEquals("ERROR : No photo to analyse, please take a picture.")
  //            composeTestRule.waitForIdle()
  //
  //            composeTestRule.waitUntil(timeoutMillis = 25000) {
  //                composeTestRule.onNodeWithTag("Error Snack Bar").isNotDisplayed()
  //            }
  //
  //            photoButton{
  //                assertIsDisplayed()
  //                performClick()
  //            }
  //        }
  //
  //        gotoDisplayPicture()
  //        ComposeScreen.onComposeScreen<DisplayPictureScreen>(composeTestRule) {
  //
  //            mlBarcodeButton {
  //                assertIsDisplayed()
  //                performClick()
  //            }
  //        }
  //        goToCameraScreen()
  //
  //
  //        ComposeScreen.onComposeScreen<CameraScreen>(composeTestRule) {
  //
  //            composeTestRule.onNodeWithTag("Error Snack Bar").assertIsDisplayed()
  //            composeTestRule
  //                .onNodeWithTag("Error Snack Bar")
  //                .onChild()
  //                .assertTextEquals("Failed to identify barcode, please try again.")
  //            composeTestRule.waitForIdle()
  //
  //            composeTestRule.waitUntil(timeoutMillis = 25000) {
  //                composeTestRule.onNodeWithTag("Error Snack Bar").isNotDisplayed()
  //            }
  //
  //        }
  //    }
}
