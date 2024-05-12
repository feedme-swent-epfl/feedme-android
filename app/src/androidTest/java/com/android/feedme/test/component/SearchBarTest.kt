package com.android.feedme.test.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.feedme.model.data.ProfileRepository
import com.android.feedme.model.data.RecipeRepository
import com.android.feedme.model.viewmodel.SearchViewModel
import com.android.feedme.ui.component.SearchBarFun
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.google.firebase.firestore.FirebaseFirestore
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)

  @Before
  fun init() {
    RecipeRepository.initialize(mockFirestore)
    ProfileRepository.initialize(mockFirestore)
  }

  @Test
  fun checkSearchBarDisplayed() {

    composeTestRule.setContent {
      SearchBarFun(Route.HOME, mockk<NavigationActions>(relaxed = true), SearchViewModel())
    }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("SearchBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Placeholder Text", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Filter Icon").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Search Icon").assertIsDisplayed().performClick()

    // Wait for the search bar to be active
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithContentDescription("Search Icon Button").assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Close Icon").assertIsDisplayed().performClick()
  }
}
