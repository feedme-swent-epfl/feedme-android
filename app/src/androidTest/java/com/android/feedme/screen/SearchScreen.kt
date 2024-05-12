package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SearchScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SearchScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("SearchScreen") }) {

  val topBar: KNode = child { hasTestTag("TopBarNavigation") }
  val bottomBar: KNode = child { hasTestTag("BottomNavigationMenu") }
  val tabRow: KNode = child { hasTestTag("TabRow") }
  val tabRecipes: KNode = child { hasTestTag("TabRecipes") }
  val tabAccounts: KNode = child { hasTestTag("TabAccounts") }
  val emptyListDisplay: KNode = child { hasTestTag("EmptyList") }
  val filteredListDisplay: KNode = child { hasTestTag("FilteredList") }
  val noRecipesText: KNode = child { hasTestTag("NoRecipes") }
  val noAccountsText: KNode = child { hasTestTag("NoAccounts") }
  val recipeCard: KNode = child { hasTestTag("RecipeCard") }
}
