package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.screen.SearchScreen
import com.android.feedme.ui.home.SearchScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  private lateinit var searchViewModel: SearchViewModel
  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var recipeViewModel: RecipeViewModel

  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  private val dummyRecipe =
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
          userid = "username",
          "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

  @Before
  fun setup() {
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
    recipeRepository = RecipeRepository.instance
    profileRepository = ProfileRepository.instance

    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns
        Profile(id = "ID_DEFAULT_1")

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)

    searchViewModel = SearchViewModel()
    profileViewModel = ProfileViewModel()
    recipeViewModel = RecipeViewModel()
  }

  @Test
  fun checkEmptySearchScreen() {
    goToSearchScreen()
    ComposeScreen.onComposeScreen<SearchScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      bottomBar { assertIsDisplayed() }
      tabRow { assertIsDisplayed() }
      emptyListDisplay { assertIsDisplayed() }
      noRecipesText { assertIsDisplayed() }
      tabAccounts {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitForIdle()
      noAccountsText { assertIsDisplayed() }
      tabRecipes {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule.waitForIdle()
    }
  }

  @Test
  fun checkNotEmptySearchScreen() {
    goToSearchScreen(false)
    ComposeScreen.onComposeScreen<SearchScreen>(composeTestRule) {
      tabRow { assertIsDisplayed() }
      filteredListDisplay { assertIsDisplayed() }
      recipeCard { assertIsDisplayed() }
    }
  }

  private fun goToSearchScreen(isEmpty: Boolean = true) {
    if (!isEmpty) {
      searchViewModel.setFilteredRecipes(listOf(dummyRecipe))
      searchViewModel.setFilteredProfiles(listOf(Profile()))
    }
    val mockNavAction = mockk<NavigationActions>(relaxed = true)
    composeTestRule.setContent {
      SearchScreen(Route.HOME, mockNavAction, searchViewModel, recipeViewModel, profileViewModel)
    }
    composeTestRule.waitForIdle()
  }
}
