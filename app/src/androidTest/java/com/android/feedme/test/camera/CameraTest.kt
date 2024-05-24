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
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTest : TestCase() {
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
    goToCameraScreen()

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

  private fun goToCameraScreen() {
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent {
      CameraScreen(navActions, mockk<InputViewModel>(), cameraViewModel)
    }
    composeTestRule.waitForIdle()
  }
}
