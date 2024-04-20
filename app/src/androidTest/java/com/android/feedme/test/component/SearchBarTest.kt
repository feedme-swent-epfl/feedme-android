package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.ui.component.SearchBarFun
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkSearchBarDisplayed() {

    composeTestRule.setContent { SearchBarFun() }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("SearchBar").assertIsDisplayed()
  }
}
