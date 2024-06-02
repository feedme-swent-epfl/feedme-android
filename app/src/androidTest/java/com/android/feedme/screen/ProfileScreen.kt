package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/** This class represents the Profile Screen and the elements it contains. */
class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ProfileScreen") }) {

  // Structural elements of the UI
  val topBarProfile: KNode = child { hasTestTag("TopBarNavigation") }
  val bottomBarProfile: KNode = child { hasTestTag("BottomNavigationMenu") }

  val profileBox: KNode = child { hasTestTag("ProfileBox") }
  val profileName: KNode = child { hasTestTag("ProfileName") }
  val profileIcon: KNode = child { hasTestTag("ProfileIcon") }
  val profileBio: KNode = child { hasTestTag("ProfileBio") }
  val followerDisplayButton: KNode = child { hasTestTag("FollowerDisplayButton") }
  val followingDisplayButton: KNode = child { hasTestTag("FollowingDisplayButton") }
  val editButton: KNode = child { hasTestTag("EditButton") }
  val followerButton: KNode = child { hasTestTag("FollowButton") }
  val followingButton: KNode = child { hasTestTag("FollowingButton") }
  val tabRow: KNode = child { hasTestTag("TabRow") }
  val recipeSmall: KNode = child { hasTestTag("RecipeSmallCard") }
  val addRecipe: KNode = child { hasTestTag("AddRecipeButton") }
}
