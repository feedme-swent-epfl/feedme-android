package com.android.feedme.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.HomeViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.screen.LandingScreen
import com.android.feedme.ui.home.LandingPage
import com.android.feedme.ui.navigation.NavigationActions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel

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
                      ingredient = Ingredient("Tomato", "tomatoID", false, false))),
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
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)

    ProfileRepository.initialize(mockFirestore)
    profileRepository = ProfileRepository.instance

    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns
        Profile(id = "ID_DEFAULT_1")

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)
    profileViewModel = ProfileViewModel()
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
    goToLandingScreen()

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      searchBar {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithContentDescription("Search Icon Button").performClick()
      composeTestRule.waitForIdle()
    }
  }

  @Test
  fun savedRecipesFunctionality() {
    goToLandingScreen()

    ComposeScreen.onComposeScreen<LandingScreen>(composeTestRule) {
      saveIcon {
        assertIsDisplayed()
        performClick()
      }
    }
    composeTestRule
        .onAllNodesWithContentDescription("Bookmark Icon on Recipe Card")[0]
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
  }

  private fun goToLandingScreen() {
    profileViewModel.setViewingProfile(Profile(id = "ID_DEFAULT_1"))
    val landingViewModel = HomeViewModel()
    landingViewModel.setRecipes(listOf(recipe1, recipe2))
    composeTestRule.setContent {
      LandingPage(
          mockk<NavigationActions>(relaxed = true),
          RecipeViewModel(),
          landingViewModel,
          profileViewModel,
          SearchViewModel())
    }
    composeTestRule.waitForIdle()
  }
}
