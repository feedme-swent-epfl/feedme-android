package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RecipeInputTestScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RecipeInputTestScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("RecipeInputScreen") }) {
  val validateRecipe: KNode = child { hasTestTag("ValidateRecipeButton") }
  val recipePicture: KNode = child { hasTestTag("RecipePicture") }
}
