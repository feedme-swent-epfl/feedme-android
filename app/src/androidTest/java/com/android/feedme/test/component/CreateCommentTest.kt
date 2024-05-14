package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.ui.component.CreateComment
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateCommentTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testCreateCommentEverythingDisplayed() {
    composeTestRule.setContent { CreateComment() }

    composeTestRule.onNodeWithTag("OuterBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InnerCol").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PhotoIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TitleField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RatingField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RatingStar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DescriptionField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DeleteButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton").assertIsDisplayed()
  }
}
