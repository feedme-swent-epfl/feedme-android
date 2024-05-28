package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GeneratedRecipesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GeneratedRecipesScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("GeneratedRecipesScreen") }) {

  val topBar: KNode = child { hasTestTag("TopBarNavigation") }
  val bottomBar: KNode = child { hasTestTag("BottomNavigationMenu") }
  val emptyListDisplay: KNode = child { hasTestTag("EmptyList") }
  val generatedListDisplay: KNode = child { hasTestTag("GeneratedList") }
  val noRecipesText: KNode = child { hasTestTag("NoRecipes") }
  val recipeCard: KNode = child { hasTestTag("RecipeCard") }
}
