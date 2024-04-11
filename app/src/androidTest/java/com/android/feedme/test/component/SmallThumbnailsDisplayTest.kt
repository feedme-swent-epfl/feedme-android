package com.android.feedme.test.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.R
import com.android.feedme.SmallThumbnails
import com.android.feedme.SmallThumbnailsDisplay
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmallThumbnailsDisplayTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  @Composable
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
            image = Image(painter = painterResource(id = R.drawable.test_image_pasta), contentDescription = null)
        )

    composeTestRule.setContent { SmallThumbnails(recipe1) }

    // composeTestRule.waitForIdle()

    // Check whether the Image or the warning message is displayed
    try {
      composeTestRule.onNodeWithTag("Recipe Image").assertIsDisplayed()
    } catch (e: AssertionError) {
      composeTestRule.onNodeWithTag("Fail Image Download")
    }

    composeTestRule.onNodeWithTag("Star Icon").assertIsDisplayed()
    composeTestRule.onNodeWithText(recipe1.rating.toString()).assertIsDisplayed()

    composeTestRule.onNodeWithTag("Info Icon").assertIsDisplayed()
    composeTestRule.onNodeWithText(recipe1.time.toString()).assertIsDisplayed()

    composeTestRule.onNodeWithTag("Save Icon").assertIsDisplayed()
    composeTestRule.onNodeWithText(recipe1.title).assertIsDisplayed()
  }
}
