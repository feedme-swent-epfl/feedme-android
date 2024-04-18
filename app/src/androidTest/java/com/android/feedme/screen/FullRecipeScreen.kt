package com.android.feedme.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.RecipeFullDisplay
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullRecipeScreen {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun checkfullRecipeDisplay() {
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

    composeTestRule.setContent { RecipeFullDisplay(recipe = recipe1) }

    composeTestRule.onNodeWithTag("Scaffold").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LazyColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Recipe Image")

    composeTestRule.onNodeWithTag("Time Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Rating Icon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Horizontal Divider 1").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Ingredient Title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Ingredient Description").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Horizontal Divider 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Step Title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Step Description").assertIsDisplayed()
  }
}

/*val recipe1 =
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
        ),
        IngredientMetaData(
            quantity = 2.0,
            measure = MeasureUnit.KG,
            ingredient = Ingredient("Beef Meet", "Meat", "meatId")
        )
    ),
    steps =
    listOf(
        Step(
            1,
            "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                    "\n" +
                    "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
            "Make the Meat Sauce"),
        Step(
            2,
            "Melt the butter in a medium pot then add the flour. Keep stirring to cook the flour for at least 5 minutes, but don’t let it brown. Pour in a little of the milk, and stir quickly to incorporate. \n" +
                    "\n" +
                    "Continue stirring and adding milk a little at a time. Once all the milk is mixed into the flour and butter mixture, add more.",
            "Make the Bechamel Sauce (while the sauce is simmering)"),
        Step(
            3,
            "Boil the noodles in plenty of salted water, making sure to keep moving them so they don’t stick together. Remove the noodles from the heat 5 minutes BEFORE the instructed time on the box." +
                    "Reserve about 2 cups of the pasta water. Drain most of the water and then fill the pot with cold water just to cover the noodles. This will stop the pasta from cooking further.",
            "Cook the Noodles (Skip this step if using no-cook lasagna noodles)")
    ),
    tags = listOf("Meat"),
    time = 1.15,
    rating = 4.5,
    userid = "@PasDavid",
    difficulty = "Intermediate",
    "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")*/
