package com.android.feedme.test.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.TopBarNavigation
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopBarNavigationTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testTopBarNavigationEverythingDisplayed() {
    // Create NavigationActions instance with the mock NavController
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true

    composeTestRule.setContent {
      TopBarNavigation(
          title = "Test",
          navAction = navActions,
          rightIcon = Icons.Default.SaveAlt,
          rightIconOnClickAction = {})
    }

    composeTestRule.onNodeWithTag("TopBarNavigation").assertIsDisplayed()

    composeTestRule.onNodeWithTag("LeftIconBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LeftIconButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule.onNodeWithTag("TitleBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TitleText").assertIsDisplayed()

    composeTestRule.onNodeWithTag("RightIconBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RightIconButton").assertIsDisplayed().assertHasClickAction()
  }

  @Test
  fun testTopBarNavigationRightIconDoesNotAppear() {
    // Create NavigationActions instance with the mock NavController
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true

    composeTestRule.setContent {
      TopBarNavigation(
          title = "Test", navAction = navActions, rightIcon = null, rightIconOnClickAction = {})
    }

    composeTestRule.onNodeWithTag("TopBarNavigation").assertIsDisplayed()

    composeTestRule.onNodeWithTag("LeftIconBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LeftIconButton").assertIsDisplayed().assertHasClickAction()

    composeTestRule.onNodeWithTag("TitleBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TitleText").assertIsDisplayed()

    composeTestRule.onNodeWithTag("RightIconBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RightIconButton").assertIsNotDisplayed().assertHasNoClickAction()
  }

  @Test
  fun testTopBarNavigationLeftIconDoesNotAppear() {
    // Create NavigationActions instance with the mock NavController
    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true

    composeTestRule.setContent {
      TopBarNavigation(
          title = "Test",
          navAction = null,
          rightIcon = Icons.Default.SaveAlt,
          rightIconOnClickAction = {})
    }

    composeTestRule.onNodeWithTag("TopBarNavigation").assertIsDisplayed()

    composeTestRule.onNodeWithTag("LeftIconBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LeftIconButton").assertIsNotDisplayed().assertHasNoClickAction()

    composeTestRule.onNodeWithTag("TitleBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TitleText").assertIsDisplayed()

    composeTestRule.onNodeWithTag("RightIconBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RightIconButton").assertIsDisplayed().assertHasClickAction()
  }
}
