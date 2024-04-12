package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Landing Screen and the elements it contains. */
class NotImplementedScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<NotImplementedScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("NotImplementedScreen") }) {

  // Structural elements of the UI
  val topBarLanding: KNode = child { hasTestTag("TopBarNavigation") }
  val bottomBarLanding: KNode = child { hasTestTag("BottomNavigationMenu") }
  val middleText: KNode = child { hasTestTag("Text") }
}
