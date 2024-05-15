package com.android.feedme.test

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.model.viewmodel.RecipeViewModel
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.screen.SavedRecipesScreen
import com.android.feedme.ui.home.SavedRecipesScreen
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
class SavedRecipesScreen : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
  private val navAction = mockk<NavigationActions>(relaxed = true)
  private lateinit var profileViewModel: ProfileViewModel
  private val searchViewModel = mockk<SearchViewModel>(relaxed = true)
  private val recipeViewModel = mockk<RecipeViewModel>(relaxed = true)


  @Before
  fun init() {
    ProfileRepository.initialize(mockFirestore)
    profileViewModel = ProfileViewModel()
  }

  @Test
  fun mainComponentsAreDisplayed() {
    ComposeScreen.onComposeScreen<SavedRecipesScreen>(composeTestRule) {
      composeTestRule.setContent { SavedRecipesScreen(navAction, profileViewModel, searchViewModel, recipeViewModel) }
      composeTestRule.onNodeWithTag("SavedScreen").assertIsDisplayed()
      composeTestRule.onNodeWithTag("SavedScreenBox").assertIsDisplayed()
      composeTestRule.onNodeWithTag("SavedScreenText").assertIsDisplayed()
    }
  }
}
