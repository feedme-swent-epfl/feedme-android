package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.ui.component.LoadMoreButton
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadMoreButtonTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkLoadMoreButtonDisplayed() {
    // Check if the Load More button is displayed
    composeTestRule.setContent { LoadMoreButton {} }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Load More Button").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("Load More Text", useUnmergedTree = true).assertIsDisplayed()
  }
}
