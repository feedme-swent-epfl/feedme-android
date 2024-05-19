package com.android.feedme.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
import com.android.feedme.ui.component.RecipeFullDisplay
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullRecipeTest : TestCase() {
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
          rating = 4.5,
          userid = "9vu1XpyZwrW5hSvEpHuuvcVVgiv2",
          "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavActions = mockk<NavigationActions>(relaxed = true)

  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  // Avoid re-creating a viewModel for every test
  private lateinit var recipeViewModel: RecipeViewModel
  private lateinit var profileViewModel: ProfileViewModel

  private lateinit var recipeRepository: RecipeRepository
  private lateinit var profileRepository: ProfileRepository

  @Before
  fun setUpMocks() {
    every { mockNavActions.canGoBack() } returns true

    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
    recipeRepository = RecipeRepository.instance
    profileRepository = ProfileRepository.instance

    every { mockFirestore.collection("recipes") } returns mockCollectionReference
    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference
    every { mockDocumentReference.id } returns ""
    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Recipe::class.java) } returns recipe
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns
        Profile(id = "ID_DEFAULT_1")

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)

    recipeViewModel = RecipeViewModel()

    recipeViewModel.selectRecipe(recipe)

    profileViewModel = ProfileViewModel()
  }

  @Test
  fun checkFullRecipeDisplay() {
    goToFullRecipeScreen()

    // Check whether the Image or the warning message is displayed
    try {
      composeTestRule.onNodeWithTag("Recipe Image").assertIsDisplayed()
    } catch (e: AssertionError) {
      composeTestRule.onNodeWithText("Fail Image Download")
    }
    composeTestRule.onNodeWithTag("General Infos Row").assertIsDisplayed()

    composeTestRule
        .onNodeWithContentDescription("Star Icon", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("Text Rating").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Horizontal Divider 1").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Ingredient Title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Ingredient Description").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Horizontal Divider 2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Step Title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Step Description").assertIsDisplayed()
  }

  private fun goToFullRecipeScreen() {
    profileViewModel.setViewingProfile(Profile(id = "ID_DEFAULT_1"))
    composeTestRule.setContent {
      RecipeFullDisplay(Route.HOME, mockNavActions, recipeViewModel, profileViewModel)
    }
    composeTestRule.waitForIdle()
  }
}
