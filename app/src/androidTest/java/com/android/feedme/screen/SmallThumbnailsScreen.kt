package com.android.feedme.screen

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
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
                    ingredient = Ingredient("Tomato", "Vegetables", "tomatoID")
                )
            ),
            steps = listOf(Step(1, "a", "Step1")),
            tags = listOf("Meat"),
            time = 1.15,
            rating = 4.5,
            userid = "PasDavid",
            difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

    @Test
    fun checkThumbnailsDisplay() {

        // Launch the activity
        composeTestRule.setContent {
            SmallThumbnailsDisplay(
                listRecipe = listOf(recipe1,recipe1,recipe1,recipe1,recipe1)
            )
        }

        // Check if the thumbnails are displayed
        composeTestRule.onNodeWithTag("Recipe Image").assertIsDisplayed()

        // Check if the ratings are displayed correctly
        composeTestRule.onNodeWithTag("Rating").assertTextEquals("4.5")
        composeTestRule.onNodeWithTag("Rating").assertTextEquals("3.8")
        // Add assertions for other ratings as needed

        // Check if the cooking times are displayed correctly
        composeTestRule.onNodeWithTag("Time").assertTextEquals("15")
        composeTestRule.onNodeWithTag("Time").assertTextEquals("20")
        // Add assertions for other cooking times as needed

        // Check if the titles are displayed correctly
        composeTestRule.onNodeWithTag("Title").assertTextEquals("Recipe 1")
        composeTestRule.onNodeWithTag("Title").assertTextEquals("Recipe 2")
        // Add assertions for other titles as needed
    }
}