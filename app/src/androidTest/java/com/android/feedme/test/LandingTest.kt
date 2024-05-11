package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.screen.LandingScreen
import com.android.feedme.ui.home.LandingPage
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  private val recipe =
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
          time = 45.0,
          rating = 4.5,
          userid = "username",
          difficulty = "Intermediate",
          "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

  @Before
  fun init() {
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
  }

  @Test
  fun mainComponentsAreDisplayed() {
    goToLandingScreen()

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      composeTestRule.waitForIdle()

      completeScreen.assertIsDisplayed()

      topBarLanding { assertIsDisplayed() }

      bottomBarLanding { assertIsDisplayed() }

      recipeList { assertIsDisplayed() }

      recipeCard {
        assertIsDisplayed()
        assertHasClickAction()
      }

      saveIcon {
        assertIsDisplayed()
        assertHasClickAction()
      }

      userName {
        assertIsDisplayed()
        assertHasClickAction()
      }

      shareIcon { assertIsDisplayed() }

      ratingButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      completeScreen { assertIsDisplayed() }
    }
  }

  @Test
  fun searchBarFunctionality() {
    goToLandingScreen(false)

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      searchBar {
        assertIsDisplayed()
        performClick()
      }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Search Icon Button").performClick()
    }
  }

  private fun goToLandingScreen(fetchRecipes: Boolean = true) {
    val landingViewModel = HomeViewModel()
    if (fetchRecipes) {
      landingViewModel.setRecipes(listOf(recipe, recipe, recipe))
    } else {
      landingViewModel.initialSearchQuery = "Tasty"
    }
    composeTestRule.setContent {
      LandingPage(mockk<NavigationActions>(relaxed = true), RecipeViewModel(), landingViewModel)
    }
    composeTestRule.waitForIdle()
  }
}
