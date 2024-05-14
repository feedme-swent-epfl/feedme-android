package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SavedRecipesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SavedRecipesScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SavedRecipesScree") }) {

  // Structural elements of the UI
  val savedScreen: KNode = child { hasTestTag("SavedScreen") }
}
