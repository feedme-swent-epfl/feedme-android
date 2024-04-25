package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Camera Screen and the elements it contains. */
class CameraScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CameraScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CameraScreen") }) {

  // Structural elements of the UI
  val photoButton: KNode = child { hasTestTag("PhotoButton") }
  val galleryButton: KNode = child { hasTestTag("GalleryButton") }
  val cameraPreview: KNode = child { hasTestTag("CameraPreview") }
  val MLButton: KNode = child { hasTestTag("MLButton") }
}
