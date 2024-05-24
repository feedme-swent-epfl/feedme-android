package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.ui.component.SmallThumbnailsDisplay
import com.android.feedme.ui.navigation.NavigationActions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
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
  private val mockDocumentReference = mockk<DocumentReference>(relaxed = true)
  private val mockCollectionReference = mockk<CollectionReference>(relaxed = true)
  private var mockDocumentSnapshot = mockk<DocumentSnapshot>(relaxed = true)

  private val recipe1 =
      Recipe(
          recipeId = "lasagna1",
          title = "Tasty Lasagna",
          description = "a",
          ingredients =
              listOf(
                  IngredientMetaData(
                      quantity = 2.0,
                      measure = MeasureUnit.ML,
                      ingredient = Ingredient("Tomato", "tomatoID", false, false))),
          steps = listOf(Step(1, "a", "Step1")),
          tags = listOf("Meat"),
          rating = 4.5,
          userid = "PasDavid",
      )

  @Before
  fun init() {
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
  }

  @Test
  fun checkThumbnailsDisplay() {
    every { mockFirestore.collection("profiles") } returns mockCollectionReference
    every { mockCollectionReference.document(any()) } returns mockDocumentReference

    every { mockDocumentReference.get() } returns Tasks.forResult(mockDocumentSnapshot)
    every { mockDocumentSnapshot.toObject(Profile::class.java) } returns
        Profile(id = "ID_DEFAULT_1")

    every { mockDocumentReference.set(any()) } returns Tasks.forResult(null)

    composeTestRule.setContent {
      SmallThumbnailsDisplay(listOf(recipe1), navMock, RecipeViewModel())
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("RecipeSmallCard").assertIsDisplayed()

    composeTestRule.onNodeWithTag("Fail Image Download", useUnmergedTree = true).assertIsDisplayed()

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
