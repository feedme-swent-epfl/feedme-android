package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.screen.FindRecipeScreen
import com.android.feedme.ui.find.FindRecipeScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindRecipeTest : TestCase() {
  // @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
  @get:Rule val composeTestRule = createComposeRule()

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
    }
  }

  private fun goToFindRecipeScreen() {
    val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
    ProfileRepository.initialize(mockFirestore)
    RecipeRepository.initialize(mockFirestore)
    IngredientsRepository.initialize(mockFirestore)
    composeTestRule.setContent { FindRecipeScreen(mockk<NavigationActions>(), InputViewModel()) }
    composeTestRule.waitForIdle()
  }
}
