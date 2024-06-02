package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Landing Screen and the elements it contains. */
class LandingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LandingScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("LandingScreen") }) {

  // Structural elements of the UI
  val topBarLanding: KNode = child { hasTestTag("TopBarNavigation") }
  val bottomBarLanding: KNode = child { hasTestTag("BottomNavigationMenu") }
  val completeScreen: KNode = child { hasTestTag("CompleteScreen") }
  val searchBar: KNode = child { hasTestTag("SearchBar") }
  val recipeList: KNode = child { hasTestTag("RecipeList") }
  val recipeCard: KNode = child { hasTestTag("RecipeCard") }
  val userName: KNode = child { hasTestTag("UserName") }
  val saveIcon: KNode = child { hasTestTag("SaveIcon") }
  val levelIcon: KNode = child { hasTestTag("RecipeDifficulty") }
  val ratingButton: KNode = child { hasTestTag("Rating") }
}
