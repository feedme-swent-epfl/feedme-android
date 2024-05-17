package com.android.feedme.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.screen.SavedRecipesScreen
import com.android.feedme.ui.home.SavedRecipesScreen
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
class SavedRecipesTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val navAction = mockk<NavigationActions>(relaxed = true)
  private lateinit var profileViewModel: ProfileViewModel
  private val searchViewModel = mockk<SearchViewModel>(relaxed = true)
  private val recipeViewModel = mockk<RecipeViewModel>(relaxed = true)

  private val recipe1 =
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
          rating = 4.5,
          userid = "9vu1XpyZwrW5hSvEpHuuvcVVgiv2",
          imageUrl =
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

  private val recipe2 =
      Recipe(
          recipeId = "pasta1",
          title = "Creamy Carbonara",
          description =
              "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
          ingredients =
              listOf(
                  IngredientMetaData(
                      quantity = 2.0,
                      measure = MeasureUnit.ML,
                      ingredient = Ingredient("Pasta", "Vegetable", "pastaID"))),
          steps =
              listOf(
                  Step(
                      1,
                      "Add the half and half to the skillet and bring to a simmer. Whisk the egg yolks into the sauce followed by the Parmesan cheese. Stir in the black pepper. Taste for salt and season if needed.",
                      "Make the Sauce")),
          tags = listOf("Meat"),
          rating = 4.2,
          userid = "9vu1XpyZwrW5hSvEpHuuvcVVgiv2",
          imageUrl =
              "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
    profileViewModel = ProfileViewModel()
  }

  @Test
  fun mainComponentsEmptyAreDisplayed() {
    ComposeScreen.onComposeScreen<SavedRecipesScreen>(composeTestRule) {
      composeTestRule.setContent {
        SavedRecipesScreen(navAction, profileViewModel, searchViewModel, recipeViewModel)
      }
      composeTestRule.onNodeWithTag("SavedScreen").assertIsDisplayed()
      composeTestRule.onNodeWithTag("SavedScreenBox").assertIsDisplayed()
      composeTestRule.onNodeWithTag("SavedScreenText").assertIsDisplayed()
    }
  }
  /* WILL BE IMPLEMENTED SOON
  @Test
  fun mainComponentsNotEmptyAreDisplayed() {
    profileViewModel.addSavedRecipes(listOf(recipe1, recipe2))
    composeTestRule.setContent {
      SavedRecipesScreen(navAction, profileViewModel, searchViewModel, recipeViewModel)
    }
  }*/
}
