package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RecipeStepInputTestScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RecipeStepInputTestScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("StepList") }) {
  val titleField: KNode = child { hasTestTag("StepInputTitle") }
  val expandButton: KNode = child { hasTestTag("StepInputExpand") }
  val deleteButton: KNode = child { hasTestTag("StepInputDelete") }
  val descriptionField: KNode = child { hasTestTag("StepInputDescription") }
  val titleError: KNode = child { hasTestTag("StepInputTitleError") }
  val descriptionError: KNode = child { hasTestTag("StepInputDescriptionError") }
  val titleList: KNode = child { hasTestTag("StepListTitle") }
  val addStepButton: KNode = child { hasTestTag("AddStepButton") }
}
