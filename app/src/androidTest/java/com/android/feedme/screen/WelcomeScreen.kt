package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class WelcomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<WelcomeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ProfileScreen") })
    {

    val mainBox: KNode = child { hasTestTag("MainBox") }
    val innerBox: KNode = child { hasTestTag("InnerBox") }
    val innerColumn: KNode = child { hasTestTag("InnerColumn") }
    val welcomeText: KNode = child { hasTestTag("WelcomeText") }
    val noAccTest: KNode = child { hasTestTag("NoAccTest") }
    val welcSpacer: KNode = child { hasTestTag("WelcSpacer") }
    val outlinedButton: KNode = child { hasTestTag("OutlinedButton") }
    val logoImage: KNode = child { hasTestTag("LogoImage") }
}