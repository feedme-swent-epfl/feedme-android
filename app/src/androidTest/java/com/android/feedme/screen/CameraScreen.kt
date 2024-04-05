package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CameraScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CameraScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("CameraScreen") }
    ) {

    // Structural elements of the UI
    val photoButton: KNode = child { hasTestTag("PhotoButton") }
    val galleryButton : KNode = child { hasTestTag("GalleryButton") }

    val emptyGalleryText: KNode = child { hasTestTag("EmptyGalleryText") }
    val photos: KNode = child { hasTestTag("Photo") }
}