package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RecipeInputTestScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RecipeInputTestScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("RecipeInputScreen") }) {

        val topBar: KNode = child { hasTestTag("TopBarNavigation") }
    val bottomBar: KNode = child { hasTestTag("BottomNavigationMenu") }
    val validateRecipe: KNode = child { hasTestTag("ValidateRecipeButton") }
    val recipeInputBox: KNode = child { hasTestTag("RecipeInputBox") }
    val recipePicture: KNode = child { hasTestTag("RecipePicture") }
    val titleInput: KNode = child { hasTestTag("RecipeTitleInput") }
    val ingredientsInput: KNode = child { hasTestTag("LazyList") }
    val stepsInput: KNode = child { hasTestTag("stepInput") }
}
