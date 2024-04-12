package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.LandingScreen
import com.android.feedme.ui.LandingPage
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingTest : TestCase() {
  // @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule val composeTestRule = createComposeRule()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  // @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java) TODO: Fix testing
  // with navigation

  /*@Before
  fun setup() {
  TODO: Fix testing with navigation

   System.setProperty("isRunningTest", "true")
   val mockGoogleSignInClient = mockk<GoogleSignInClient>(relaxed = true)
   MockServiceLocator.registerService("GoogleSignInClient", mockGoogleSignInClient)
   val mockGoogleSignInAccount = mockk<GoogleSignInAccount>(relaxed = true)
   every { mockGoogleSignInClient.silentSignIn() } returns Tasks.forResult(mockGoogleSignInAccount)

    }*/

  @Test
  fun mainComponentsAreDisplayed() {
    goToLandingScreen()

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      topBarLanding { assertIsDisplayed() }

      bottomBarLanding { assertIsDisplayed() }

      recipeList { assertIsDisplayed() }

      saveIcon {
        assertIsDisplayed()
        assertHasClickAction()
      }

      userName {
        assertIsDisplayed()
        assertHasClickAction()
      }

      ratingButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      filterClick {
        assertIsDisplayed()
        assertHasClickAction()
      }

      completeScreen { assertIsDisplayed() }
    }
  }

  private fun goToLandingScreen() {
    composeTestRule.setContent { LandingPage(mockk<NavigationActions>()) }
    composeTestRule.waitForIdle()

    // TODO: Fix testing with navigation
    /*// This function is used to navigate from LoginScreen to LandingScreen
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      loginButton {
        performClick()
      }
      composeTestRule.waitForIdle()

      // Assert that the Google SignIn Client was called
      //verify { mockGoogleSignInClient.silentSignIn() }

      composeTestRule.waitForIdle()
    }*/
  }
}
