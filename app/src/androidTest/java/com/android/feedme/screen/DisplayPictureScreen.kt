package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Camera Screen and the elements it contains. */
class DisplayPictureScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DisplayPictureScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("DisplayPicture") }) {

  // Structural elements of the UI
  val mlTextButton: KNode = child { hasTestTag("MLTextButton") }
  val mlBarcodeButton: KNode = child { hasTestTag("MLBarcodeButton") }
}
