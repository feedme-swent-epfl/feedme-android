package com.android.feedme.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.feedme.screen.CameraScreen
import com.android.feedme.ui.CreateScreen
import com.android.feedme.ui.LandingPage
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserFlowTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // Grant camera permission for tests
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

  @Test
  fun userFlowFromHomePageNavigateToCameraAndTakePhoto() {
    // Set Navigation
    composeTestRule.setContent {
      val navController = rememberNavController()
      val navigationActions = NavigationActions(navController)

      // Set up the navigation graph
      NavHost(navController = navController, startDestination = Route.HOME) {
        composable(Route.HOME) { LandingPage(navigationActions) }
        composable(Route.CREATE) { CreateScreen(navigationActions) }
        composable(Route.CAMERA) { CameraScreen(navigationActions) }
      }
    }

    // From Home Page go to Camera
    composeTestRule.onNodeWithText("Create").assertIsDisplayed().performClick()

    // Open Camera
    composeTestRule.onNodeWithTag("CameraButton").assertIsDisplayed().performClick()

    // Take Photo
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
