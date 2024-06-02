package com.android.feedme.test.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.viewmodel.AuthViewModel
import com.android.feedme.screen.LoginScreen
import com.android.feedme.ui.auth.LoginScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  // @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Test
  fun titleAndButtonAreCorrectlyDisplayed() {
    goToLoginScreen()
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Test the UI elements
      loginTitle {
        assertIsDisplayed()
        assertTextEquals("Welcome!")
      }
      loginButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  /* This Test make the CI fail, as no response of the Intent is received
    @Test
    fun googleSignInReturnsValidActivityResult() {
      goToLoginScreen()
      ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
        loginButton {
          assertIsDisplayed()
          performClick()
        }

        // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
        intended(toPackage("com.google.android.gms"))
      }
    }
  */
  private fun goToLoginScreen() {
    composeTestRule.setContent { LoginScreen(mockk<NavigationActions>(), AuthViewModel()) }
    composeTestRule.waitForIdle()
  }
}
