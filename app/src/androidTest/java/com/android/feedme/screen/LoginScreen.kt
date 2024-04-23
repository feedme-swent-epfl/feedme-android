package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Login Screen and the elements it contains. */
class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  // Structural elements of the UI
  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }
  /** A function to set the test mode for the app. */
  fun setTestMode(bool: Boolean) {
    Testing.isTestMode = bool
  }

  /**
   * A function to set the mocking for successful login.
   *
   * @param mockingSuccessfulLogin : a lambda function to mock successful login
   */
  fun setLoginMockingForTests(mockingSuccessfulLogin: () -> Unit) {
    Testing.mockSuccessfulLogin = mockingSuccessfulLogin
  }

  /** A testing object to set the test mode and mock successful login. */
  object Testing {
    var isTestMode = false
    var mockSuccessfulLogin = {}
  }
}
