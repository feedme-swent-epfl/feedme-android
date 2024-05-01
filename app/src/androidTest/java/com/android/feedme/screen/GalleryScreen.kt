package com.android.feedme.screen


import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GalleryScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GalleryScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("GalleryScreen") }) {

    // Structural elements of the UI
    val galleryButton: KNode = child { hasTestTag("GalleryButton") }
}
