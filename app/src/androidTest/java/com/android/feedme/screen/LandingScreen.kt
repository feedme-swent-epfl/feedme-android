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
  val recipeList: KNode = child { hasTestTag("RecipeList") }
  val saveIcon: KNode = child { hasTestTag("SaveIcon") }
  val userName: KNode = child { hasTestTag("UserName") }
  val ratingButton: KNode = child { hasTestTag("Rating") }
}
