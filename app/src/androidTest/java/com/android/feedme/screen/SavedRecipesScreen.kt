package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class SavedRecipesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SavedRecipesScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SavedRecipesScree") }) {}
