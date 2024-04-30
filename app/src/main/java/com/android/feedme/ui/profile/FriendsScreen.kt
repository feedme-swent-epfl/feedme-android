package com.android.feedme.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R
import com.android.feedme.model.data.Profile
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * Composable that displays either a list of followers or following based on the selected tab. It
 * includes a tabbed interface to switch between the "Followers" and "Following" lists.
 *
 * @param navigationActions Provides navigation actions for handling user interactions with the
 *   navigation bar.
 * @param profileViewModel The view model that provides the profile data.
 * @param mode Determines the initial tab selection: 0 for Followers, 1 for Following, 4242 for
 *   testing.
 */
@Composable
fun FriendsScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel = ProfileViewModel(),
    mode: Int = 0,
) {

  var selectedTabIndex by remember { mutableIntStateOf(mode) }
  val tabTitles = listOf("Followers", "Following")

  // Now, collect followers and following as state to display them
  val followers =
      if (mode == 4242 || profileViewModel.isViewingProfile())
          profileViewModel.viewingUserFollowers.collectAsState()
      else profileViewModel.currentUserFollowers.collectAsState()
  val following =
      if (mode == 4242 || profileViewModel.isViewingProfile())
          profileViewModel.viewingUserFollowing.collectAsState()
      else profileViewModel.currentUserFollowing.collectAsState()
  if (selectedTabIndex == 4242) {selectedTabIndex = 0}


    // Create mutable state lists for followers and following

    val followersM = remember { followers.value.toMutableStateList() }
    val followingM = remember { following.value.toMutableStateList() }


    DisposableEffect(navigationActions) {
        onDispose {
            // sync profile with db
            if (!profileViewModel.isViewingProfile()) {
                var def = profileViewModel.currentUserProfile.value ?: Profile()
                var followersId = followersM.toList().map { it.id }
                var followingId = followingM.toList().map { it.id }
                var profile = Profile(
                    def.id,
                    def.name,
                    def.username,
                    def.email,
                    def.description,
                    def.imageUrl,
                    followersId,
                    followingId,
                    def.filter,
                    def.recipeList,
                    def.commentList
                )
                profileViewModel.setProfile(profile)
            }
        }
    }
  Scaffold(
      modifier = Modifier
          .fillMaxSize()
          .testTag("FriendsScreen"),
      topBar = { TopBarNavigation(title = "Friends", navigationActions) },
      bottomBar = {
        BottomNavigationMenu(
            Route.PROFILE,
            { top ->
              profileViewModel.removeViewingProfile()
              navigationActions.navigateTo(top)
            },
            TOP_LEVEL_DESTINATIONS)
      },
      content = { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
          TabRow(
              selectedTabIndex = selectedTabIndex,
              containerColor = MaterialTheme.colorScheme.surface,
              contentColor = MaterialTheme.colorScheme.onSurface) {
                tabTitles.forEachIndexed { index, title ->
                  Tab(
                      text = { Text(title) },
                      selected = selectedTabIndex == index,
                      onClick = { selectedTabIndex = index },
                      modifier =
                          Modifier.testTag(if (index == 0) "TabFollowers" else "TabFollowing"))
                }
              }
          when (selectedTabIndex) {
            0 ->
                FollowersList(followersM, "FollowersList", navigationActions, profileViewModel)
            1 ->
                FollowersList(followingM, "FollowingList", navigationActions, profileViewModel)
          }
        }
      })
}

/**
 * Displays a lazy scrolling list of user profiles.
 *
 * @param profiles The list of profiles to be displayed.
 * @param tag A testing tag used for UI tests to identify the list view.
 */
@Composable
fun FollowersList(
    profiles: MutableList<Profile>,
    tag: String,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel
) {
  LazyColumn(modifier = Modifier
      .fillMaxSize()
      .testTag(tag)) {
    items(profiles) { profile ->
      FollowerCard(profile = profile,profiles, navigationActions, profileViewModel)
    }
  }
}

/**
 * A card representation for a user profile, displaying the user's picture, name, and username. It
 * also provides a 'Remove' button and an options menu for further actions.
 *
 * @param profile The profile data of the user.
 */
@Composable
fun FollowerCard(
    profile: Profile,
    profiles: MutableList<Profile>,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel
) {
  Card(
      modifier =
      Modifier
          .padding(4.dp)
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
          .testTag("FollowerCard") // Applying a semi-transparent background
      ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxWidth().clickable {
                  /*TODO Navigate to profile view of follower*/
                  profileViewModel.setViewingProfile(profile)
                  navigationActions.navigateTo(Screen.PROFILE)
                }) {
              Image(
                  painter =
                      painterResource(
                          id =
                              R.drawable
                                  .user_logo), // Assuming google_logo is your default profile icon
                  contentDescription = "Profile Image",
                  modifier = Modifier
                      .padding(horizontal = 10.dp)
                      .size(50.dp)
                      .clip(CircleShape),
              )
              Column(modifier = Modifier
                  .padding(10.dp)
                  .weight(1f)) {
                Text(text = profile.name, fontSize = 14.sp)
                Text(
                    text = "@" + profile.username,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
              }
              Spacer(modifier = Modifier.weight(0.1f))
              Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                Row {
                  Button(
                      onClick = { profiles.remove(profile)},
                      Modifier.padding(top = 4.dp, bottom = 4.dp, end = 0.dp)) {
                        Text(text = "Remove")
                      }
                  IconButton(onClick = { /* TODO: Implement action */}) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Options")
                  }
                }
              }
            }
      }
}
