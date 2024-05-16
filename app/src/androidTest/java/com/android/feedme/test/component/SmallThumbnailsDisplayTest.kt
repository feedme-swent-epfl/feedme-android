package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.android.feedme.ui.component.SmallThumbnailsDisplay
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmallThumbnailsDisplayTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val navMock = mockk<NavigationActions>()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  @Before
  fun init() {
    RecipeRepository.initialize(mockFirestore)
  }

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
            rating = 4.5,
            userid = "PasDavid",
            "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

    composeTestRule.setContent { SmallThumbnailsDisplay(listOf(recipe1), navMock) }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("RecipeSmallCard").assertIsDisplayed()

    // Check whether the Image or the warning message is displayed
    /*try {
      composeTestRule.onNodeWithTag("Recipe Image").assertIsDisplayed()
    } catch (e: AssertionError) {
      composeTestRule.onNodeWithText("Fail Image Download")
    }*/

    composeTestRule
        .onNodeWithContentDescription("Star Icon", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("Text Rating", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule
        .onNodeWithContentDescription("Save Icon", useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("Text Title", useUnmergedTree = true).assertIsDisplayed()
  }
}
