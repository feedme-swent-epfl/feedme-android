package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.CommentRepository
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.CommentViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.component.CreateComment
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateCommentTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  @Before
  fun init() {
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
    CommentRepository.initialize(mockFirestore)
  }

  @Test
  fun testCreateCommentEverythingDisplayed() {
    var bol = false
    composeTestRule.setContent {
      CreateComment(ProfileViewModel(), RecipeViewModel(), CommentViewModel()) { bol = true }
    }

    composeTestRule.onNodeWithTag("OuterBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InnerCol").assertIsDisplayed()
    composeTestRule.onNodeWithTag("FirstRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("StarRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star4").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TitleField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DescriptionField").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ButtonRow").assertIsDisplayed()
    composeTestRule.onNodeWithTag("DeleteButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton").performClick()
    assertTrue(bol)
    bol = false
    composeTestRule.onNodeWithTag("DeleteButton").performClick()
    assertTrue(bol)
  }
}
