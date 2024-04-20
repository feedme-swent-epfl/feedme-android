package com.android.feedme.test.component

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.screen.LandingScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkSearchBarDisplayed() {

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      searchBar { assertIsDisplayed() }
    }
  }
}
