package com.android.feedme.test.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.screen.GeneratedRecipesScreen
import com.android.feedme.ui.generate.GeneratedRecipesScreen
import com.android.feedme.ui.navigation.NavigationActions
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeneratedRecipesScreenTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  private val mockNavActions = mockk<NavigationActions>(relaxed = true)

  private lateinit var recipeViewModel: RecipeViewModel
  private lateinit var profileViewModel: ProfileViewModel

  @Before
  fun setup() {
    profileViewModel = ProfileViewModel()
    recipeViewModel = RecipeViewModel()
  }

  @Test
  fun testGeneratedRecipesScreen() {
    goToGeneratedRecipesScreen()
    ComposeScreen.onComposeScreen<GeneratedRecipesScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      bottomBar { assertIsDisplayed() }
      emptyListDisplay { assertIsDisplayed() }
      noRecipesText { assertIsDisplayed() }
    }
  }

  @Test
  fun testGeneratedRecipesScreenWithRecipes() {
    goToGeneratedRecipesScreen()
    ComposeScreen.onComposeScreen<GeneratedRecipesScreen>(composeTestRule) {
      topBar { assertIsDisplayed() }
      bottomBar { assertIsDisplayed() }
      generatedListDisplay { assertIsDisplayed() }
      recipeCard { assertIsDisplayed() }
    }
  }

  private fun goToGeneratedRecipesScreen() {
    composeTestRule.setContent {
      GeneratedRecipesScreen(mockNavActions, recipeViewModel, profileViewModel)
    }
  }
}
