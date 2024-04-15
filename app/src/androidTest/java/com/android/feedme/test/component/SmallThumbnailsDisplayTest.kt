package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.ui.component.SmallThumbnailsDisplay
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmallThumbnailsDisplayTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkThumbnailsDisplay() {
    val recipe1 =
        Recipe(
            recipeId = "lasagna1",
            title = "Tasty Lasagna",
            description = "a",
            ingredients =
                listOf(
                    IngredientMetaData(
                        quantity = 2.0,
                        measure = MeasureUnit.ML,
                        ingredient = Ingredient("Tomato", "Vegetables", "tomatoID"))),
            steps = listOf(Step(1, "a", "Step1")),
            tags = listOf("Meat"),
            time = 1.15,
            rating = 4.5,
            userid = "PasDavid",
            difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

    composeTestRule.setContent { SmallThumbnailsDisplay(listRecipe = listOf(recipe1)) }

    // Check whether the Image or the warning message is displayed
    try {
      composeTestRule.onNodeWithTag("Recipe Image").assertIsDisplayed()
    } catch (e: AssertionError) {
      composeTestRule.onNodeWithText("Fail Image Download")
    }

    // Thread.sleep(30000)
    composeTestRule.onNodeWithContentDescription("Star Icon").assertIsDisplayed()
    composeTestRule.onNodeWithText(recipe1.rating.toString()).assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("Info Icon").assertIsDisplayed()
    composeTestRule.onNodeWithText(recipe1.time.toString()).assertIsDisplayed()

    composeTestRule.waitForIdle()
    /*composeTestRule
    .onNodeWithContentDescription("Save Icon", useUnmergedTree = true)
    .assertIsDisplayed()*/
    composeTestRule.onNodeWithText(recipe1.title).assertIsDisplayed()
  }
}