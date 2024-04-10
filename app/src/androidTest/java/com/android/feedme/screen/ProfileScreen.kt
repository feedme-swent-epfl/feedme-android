package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Profile Screen and the elements it contains. */
class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider):
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("Profile Screen") }
    ) {
    // Structural elements of the UI
    val topBarProfile: KNode = child { hasTestTag("TopBarProfile") }
    val bottomBarProfile: KNode = child { hasTestTag("BottomBarProfile") }
    val profileBox: KNode = child { hasTestTag("ProfileBox") }
    val profileName: KNode = child { hasTestTag("ProfileName") }
    val profileIcon: KNode = child { hasTestTag("ProfileIcon") }
    val profileBio: KNode = child { hasTestTag("ProfileBio") }
    val followerButton: KNode = child { hasTestTag("FollowerButton") }
    val followingButton: KNode = child { hasTestTag("FollowingButton") }
    val editButton: KNode = child { hasTestTag("EditButton") }
    val shareButton: KNode = child { hasTestTag("ShareButton") }
}