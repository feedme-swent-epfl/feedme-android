package com.android.feedme.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.ui.home.RecipeFullDisplay
import com.android.feedme.ui.navigation.NavigationActions
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullRecipeTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkFullRecipeDisplay() {
    val recipe1 =
        Recipe(
            recipeId = "lasagna1",
            title = "Tasty Lasagna",
            description =
                "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
            ingredients =
                listOf(
                    IngredientMetaData(
                        quantity = 2.0,
                        measure = MeasureUnit.ML,
                        ingredient = Ingredient("Tomato", "Vegetables", "tomatoID"))),
            steps =
                listOf(
                    Step(
                        1,
                        "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                            "\n" +
                            "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
                        "Make the Meat Sauce")),
            tags = listOf("Meat"),
            time = 1.15,
            rating = 4.5,
            userid = "@PasDavid",
            difficulty = "Intermediate",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

    val navActions = mockk<NavigationActions>()
    every { navActions.canGoBack() } returns true
    composeTestRule.setContent { RecipeFullDisplay(navActions) }

    // Check whether the Image or the warning message is displayed
    try {
      composeTestRule.onNodeWithTag("Recipe Image").assertIsDisplayed()
    } catch (e: AssertionError) {
      composeTestRule.onNodeWithText("Fail Image Download")
    }
    composeTestRule.onNodeWithTag("General Infos Row").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Time Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Text Time").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Rating Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Text Rating").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Horizontal Divider 1").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Ingredient Title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Ingredient Description").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Horizontal Divider 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Step Title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Step Description").assertIsDisplayed()
  }
}
