package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Create Screen and the elements it contains. */
class CreateScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateScreen") }) {

  // Structural elements of the UI
  val topBarLanding: KNode = child { hasTestTag("TopBarNavigation") }
  val bottomBarLanding: KNode = child { hasTestTag("BottomNavigationMenu") }
  val cameraButton: KNode = child { hasTestTag("CameraButton") }
}