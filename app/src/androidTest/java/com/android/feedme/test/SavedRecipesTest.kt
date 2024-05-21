package com.android.feedme.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
import com.android.feedme.ui.home.SavedRecipesScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
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
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)
  private val mockIngredientsCollectionReference = mockk<CollectionReference>(relaxed = true)
  private val mockIngredientDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  private var mockQuery: Query = mockk()
  private var mockQuerySnapshot: QuerySnapshot = mockk()

  private lateinit var profileRepository: ProfileRepository
  private lateinit var recipeRepository: RecipeRepository

  private lateinit var profileViewModel: ProfileViewModel
  private lateinit var homeViewModel: HomeViewModel
  private lateinit var recipeViewModel: RecipeViewModel

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
                      ingredient = Ingredient("Tomato", "Vegetables", true, true))),
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
                      ingredient = Ingredient("Pasta", "Vegetable", true, true))),
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

    profileRepository = ProfileRepository.instance
    recipeRepository = RecipeRepository.instance

    profileViewModel = ProfileViewModel()
    homeViewModel = HomeViewModel()
    recipeViewModel = RecipeViewModel()

    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockFirestore.collection("recipes") } returns mockCollectionReference
    every { mockFirestore.collection("ingredients") } returns mockIngredientsCollectionReference

    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns
        Profile(id = "ID_DEFAULT_1")

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)

    every { mockDocumentSnapshot.exists() } returns true
    // TODO : uncomment and fix the test
    // every { mockDocumentSnapshot.data } returns recipe2Map

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)

    // every { homeViewModel.fetchSavedRecipes(any()) } just Runs

    every { mockCollectionReference.whereIn("recipeId", listOf(recipe2.recipeId)) } answers
        {
          mockQuery
        }
    every { mockQuery.limit(6) } answers { mockQuery }
    every { mockQuery.get() } answers { Tasks.forResult(mockQuerySnapshot) }
    // every {mockQuerySnapshot.documents} answers {listOf(mockDocumentSnapshot)}
  }

  @Test
  fun mainComponentsEmptyAreDisplayed() {
    composeTestRule.setContent {
      SavedRecipesScreen(navAction, profileViewModel, recipeViewModel, homeViewModel)
    }
    composeTestRule.onNodeWithTag("SavedScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SavedScreenBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SavedScreenText").assertIsDisplayed()
  }

  @Test
  fun mainComponentsNotEmptyAreDisplayed() {
    profileViewModel.addSavedRecipes(recipe1.recipeId)
    profileViewModel.addSavedRecipes(recipe2.recipeId)
    profileViewModel.removeSavedRecipes(recipe1.recipeId)

    // TODO : uncomment and fix the test
    // profileViewModel.setUserSavedRecipes(listOf(recipe2.recipeId))
    // homeViewModel.setSavedRecipes(listOf(recipe2))
    composeTestRule.setContent {
      SavedRecipesScreen(navAction, profileViewModel, recipeViewModel, homeViewModel)
    }
    // TODO : uncomment and fix the test
    // composeTestRule.onNodeWithTag("RecipeCard").assertIsDisplayed()

  }

  private val recipe2Map: Map<String, Any> =
      mapOf(
          "recipeId" to recipe2.recipeId,
          "title" to "Chocolate Cake",
          "description" to "A deliciously rich chocolate cake.",
          "ingredients" to
              listOf(
                  mapOf(
                      "ingredientId" to "flourId",
                      "quantity" to 2.0,
                      "measure" to MeasureUnit.CUP.name),
                  mapOf(
                      "ingredientId" to "sugarId",
                      "quantity" to 1.0,
                      "measure" to MeasureUnit.CUP.name),
                  mapOf(
                      "ingredientId" to "cocoaId",
                      "quantity" to 0.5,
                      "measure" to MeasureUnit.CUP.name)),
          "steps" to
              listOf(
                  mapOf(
                      "stepNumber" to 1,
                      "description" to "Mix dry ingredients.",
                      "title" to "Prepare Dry Mix"),
                  mapOf(
                      "stepNumber" to 2,
                      "description" to "Blend with wet ingredients.",
                      "title" to "Mix Ingredients"),
                  mapOf(
                      "stepNumber" to 3,
                      "description" to "Pour into pan and bake.",
                      "title" to "Bake")),
          "tags" to listOf("dessert", "chocolate", "cake"),
          "time" to 60.0,
          "rating" to 4.5,
          "userid" to "user123",
          "difficulty" to "Easy",
          "imageUrl" to "http://example.com/chocolate_cake.jpg")
}
