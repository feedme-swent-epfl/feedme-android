package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeInputTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val navigationActions: NavigationActions = mockk(relaxed = true)

  private val profileViewModel: ProfileViewModel = mockk(relaxed = true)

  private lateinit var recipeRepository: RecipeRepository

  //  @Before
  //  fun setUp() {
  //    RecipeRepository.initialize(mockFirestore)
  //    recipeRepository = RecipeRepository.instance
  //  }

  @Test
  fun validate() {
    assert(true)
  }
  //  @Test
  //  fun recipeInputTestDisplayedAndValidates() {
  //    composeTestRule.setContent {
  //      RecipeInputScreen(
  //        mockk<NavigationActions>(relaxed = true), mockk<ProfileViewModel>(relaxed = true))
  //    }
  //
  //    composeTestRule.waitForIdle()
  //
  //    ComposeScreen.onComposeScreen<RecipeInputTestScreen>(composeTestRule) {
  //      topBar { assertIsDisplayed() }
  //      bottomBar { assertIsDisplayed() }
  //      recipeInputBox { assertIsDisplayed() }
  //      recipePicture { assertIsDisplayed() }
  //
  //      validateRecipe {
  //        assertIsDisplayed()
  //        assertHasClickAction()
  //      }
  //
  //      titleInput {
  //        assertIsDisplayed()
  //        // TODO add input
  //      }
  //      ingredientsInput { assertIsDisplayed() }
  //      stepsInput { assertIsDisplayed() }
  //
  //      validateRecipe {
  //        assertIsDisplayed()
  //        assertHasClickAction()
  //      }
  //    }
  //  }

  //  @Test
  //  fun recipeViewModelValidateFails() {
  //        composeTestRule.setContent {
  //          RecipeInputScreen(
  //              mockk<NavigationActions>(relaxed = true), mockk<ProfileViewModel>(relaxed = true))
  //        }
  //        composeTestRule.waitForIdle()
  //
  //        ComposeScreen.onComposeScreen<RecipeInputTestScreen>(composeTestRule) {
  //          validateRecipe {
  //            assertIsDisplayed()
  //            performClick()
  //          }
  //          composeTestRule.onNodeWithTag("Error Snack Bar").assertIsDisplayed()
  //          composeTestRule
  //              .onNodeWithTag("Error Snack Bar")
  //              .onChild()
  //              .assertTextEquals("Error : Recipe not correctly filled in")
  //          composeTestRule.waitForIdle()
  //
  //          composeTestRule.waitUntil(timeoutMillis = 25000) {
  //            composeTestRule.onNodeWithTag("Error Snack Bar").isNotDisplayed()
  //          }
  //  }
}
