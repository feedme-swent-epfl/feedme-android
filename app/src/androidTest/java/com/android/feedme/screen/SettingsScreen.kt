package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SettingsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SettingsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SettingsScreen") }) {

  // Structural elements of the UI
  val signOutButton: KNode = child { hasTestTag("SignOutButton") }
  val deleteAccountButton: KNode = child { hasTestTag("DeleteAccountButton") }
  val alertDialogBox: KNode = child { hasText("AlertDialogBox") }
  val confirmButton: KNode = child { hasTestTag("ConfirmButton") }
  val dismissButton: KNode = child { hasTestTag("DismissButton") }
}
