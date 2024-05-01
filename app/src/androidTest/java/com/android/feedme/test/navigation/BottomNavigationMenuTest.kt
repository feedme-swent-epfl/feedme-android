package com.android.feedme.test.navigation

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavigationMenuTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavigationMenuCorrectlyDisplayed() {
    composeTestRule.setContent {
      BottomNavigationMenu(
          selectedItem = "Home", onTabSelect = {}, tabList = TOP_LEVEL_DESTINATIONS)
    }

    composeTestRule.onNodeWithTag("BottomNavigationMenu").assertIsDisplayed()

    TOP_LEVEL_DESTINATIONS.forEach { destination ->
      composeTestRule.onNodeWithTag(destination.textId).assertIsDisplayed().assertHasClickAction()
      composeTestRule.onNodeWithContentDescription(destination.textId).assertIsDisplayed()
    }
  }
}
