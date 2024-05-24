package com.android.feedme.test.camera

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.model.viewmodel.CameraViewModel
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.screen.DisplayPictureScreen
import com.android.feedme.ui.camera.DisplayPicture
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Screen
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
          GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)
      else GrantPermissionRule.grant(Manifest.permission.READ_MEDIA_IMAGES)

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
      composeTestRule.onNodeWithTag("LeftIconButton").performClick()
    }
  }

  @Test
  fun photoCorrectlyDisplayed() {
    gotoDisplayPicture()

    ComposeScreen.onComposeScreen<DisplayPictureScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("ImageToAnalyze").performClick()

      mlBarcodeButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      mlTextButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      composeTestRule.onNodeWithTag("LeftIconButton").performClick()
    }
  }

  private fun gotoDisplayPicture() {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    every { navActions.navigateTo(Screen.CAMERA) } returns Unit
    composeTestRule.setContent {
      DisplayPicture(navActions, mockk<InputViewModel>(), cameraViewModel)
    }
    composeTestRule.waitForIdle()
  }
}
