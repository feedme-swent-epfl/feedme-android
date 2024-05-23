package com.android.feedme.test

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.screen.FindRecipeScreen
import com.android.feedme.ui.find.FindRecipeScreen
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
class FindRecipeTest : TestCase() {
  // @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule val composeTestRule = createComposeRule()

  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  private lateinit var profileRepository: ProfileRepository
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun setUp() {
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
    goToFindRecipeScreen()

    ComposeScreen.onComposeScreen<FindRecipeScreen>(composeTestRule) {
      topBarLanding { assertIsDisplayed() }

      bottomBarLanding { assertIsDisplayed() }

      cameraButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

      validateButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      composeTestRule.onNodeWithTag("StrictText", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("ToggleSwitch", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertHasClickAction()
      composeTestRule.onNodeWithTag("ExtraText", useUnmergedTree = true).assertIsDisplayed()

      composeTestRule.onNodeWithTag("Dialog", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag("InfoIcon", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag("InfoText1", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag("InfoText2", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag("InfoText3", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("CheckBox", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertHasClickAction()
      composeTestRule
          .onNodeWithTag("DismissText", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertHasClickAction()
    }
  }

  private fun goToFindRecipeScreen() {
    val mockIngredientsRepository = mockk<IngredientsRepository>()
    IngredientsRepository.initialize(mockIngredientsRepository)

    val ingredient = Ingredient("Sugar", "sugarId", false, false)
    val expectedIngredientsList = listOf(ingredient)

    every { mockIngredientsRepository.getFilteredIngredients(any(), any(), any()) } answers
        {
          val onSuccess: (List<Ingredient>) -> Unit = arg(1)
          val onFailure: (Exception) -> Unit = arg(2)
          onFailure.invoke(Exception())
          onSuccess.invoke(expectedIngredientsList)
        }
    every { mockIngredientsRepository.addIngredient(any(), any(), any()) } answers
        {
          val onSuccess: (Ingredient) -> Unit = arg(1)
          val onFailure: (Exception) -> Unit = arg(2)
          onFailure.invoke(Exception())
          onSuccess.invoke(ingredient)
        }

    composeTestRule.setContent {
      FindRecipeScreen(mockk<NavigationActions>(), InputViewModel(), ProfileViewModel())
    }
    composeTestRule.waitForIdle()
  }
}
