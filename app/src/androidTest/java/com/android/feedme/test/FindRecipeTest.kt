package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientsRepository
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.screen.FindRecipeScreen
import com.android.feedme.ui.find.FindRecipeScreen
import com.android.feedme.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
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
    val mockIngredientsRepository = mockk<IngredientsRepository>()
    IngredientsRepository.initialize(mockIngredientsRepository)

    val ingredient = Ingredient("Sugar", "sugarId", false, false)
    val expectedIngredientsList = listOf(ingredient)

    every { mockIngredientsRepository.getFilteredIngredients(any(), any(), any()) } answers
        {
          val onSuccess: (List<Ingredient>) -> Unit = arg(1)
          onSuccess.invoke(expectedIngredientsList)
        }
    composeTestRule.setContent { FindRecipeScreen(mockk<NavigationActions>(), InputViewModel()) }
    composeTestRule.waitForIdle()
  }
}
