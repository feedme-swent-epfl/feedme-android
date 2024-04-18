package com.android.feedme.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class FriendsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FriendsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("FriendsScreen") }) {

  val tabFollowers: KNode = child { hasTestTag("TabFollowers") }
  val tabFollowing: KNode = child { hasTestTag("TabFollowing") }

  val followersList: KNode = child { hasTestTag("FollowersList") }
  val followingList: KNode = child { hasTestTag("FollowingList") }

  val followerCard: KNode = child {
    hasTestTag("FollowerCard")
  } // You may need to iterate through the list for multiple cards
}
