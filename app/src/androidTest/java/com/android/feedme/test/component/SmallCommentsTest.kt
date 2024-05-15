package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Comment
import com.android.feedme.ui.component.SmallCommentsDisplay
import java.util.Date
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmallCommentsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkCommentsDisplay() {
    val comment1 =
        Comment(
            "@author",
            "@author",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images",
            3.5,
            1.15,
            "Was uncooked",
            "I respected instruction, but the result wasn't great",
            Date())

    composeTestRule.setContent { SmallCommentsDisplay(listComment = listOf(comment1)) }

    // Recipe Image
    composeTestRule.onNodeWithContentDescription("Recipe Image").assertIsDisplayed()

    // Author name
    composeTestRule.onNodeWithText(comment1.commentId).assertIsDisplayed()

    // Title
    composeTestRule.onNodeWithText(comment1.title).assertIsDisplayed()

    // Description
    composeTestRule.onNodeWithText(comment1.content).assertIsDisplayed()
  }
}
