package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the Edit Profile Screen and the elements it contains within the UI. It
 * provides direct access to UI components for testing.
 */
class EditProfileTestScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditProfileTestScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EditProfileScreen") }) {

  // Structural elements of the UI for EditProfileScreen
  val editPicture: KNode = child { hasTestTag("Edit_Picture") }
  val nameInput: KNode = child { hasTestTag("NameInput") }
  val usernameInput: KNode = child { hasTestTag("UsernameInput") }
  val bioInput: KNode = child { hasTestTag("BioInput") }
  val nameError: KNode = child { hasTestTag("NameError") }
  val usernameError: KNode = child { hasTestTag("UsernameError") }
  val bioError: KNode = child { hasTestTag("BioError") }
  val saveButton: KNode = child { hasTestTag("EditSave") }
  val editProfileContent: KNode = child { hasTestTag("EditProfileContent") }
}
