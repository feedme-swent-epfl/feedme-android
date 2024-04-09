package com.android.feedme.test

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.CurrentScreen
import com.android.feedme.MainActivity
import com.android.feedme.screen.LandingScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Before
  fun setup() {
    val scenario = ActivityScenario.launch(MainActivity::class.java)
    scenario.onActivity { activity -> activity.setScreen(CurrentScreen.LANDING) }
  }

  @Test
  fun mainComponentsAreDisplayed() {
    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      // Add top bar TODO()

      bottomBar { assertIsDisplayed() }

      // Add other main components of landing page TODO()
    }
  }
}
