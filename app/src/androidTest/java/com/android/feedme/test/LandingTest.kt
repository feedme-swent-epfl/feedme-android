package com.android.feedme.test

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.screen.LandingScreen
import com.android.feedme.ui.home.LandingPage
import com.android.feedme.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  @Before
  fun init() {
    RecipeRepository.initialize(mockFirestore)
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

  private fun goToLandingScreen() {
    composeTestRule.setContent { LandingPage(mockk<NavigationActions>(relaxed = true)) }
    composeTestRule.waitForIdle()
  }
}
