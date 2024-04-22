package com.android.feedme.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.android.feedme.ui.theme.DarkGrey
import com.android.feedme.ui.theme.FollowButton
import com.android.feedme.ui.theme.FollowButtonBorder
import com.android.feedme.ui.theme.FollowingButton
import com.android.feedme.ui.theme.TextBarColor

/**
 * A composable function that generates the profile screen.
 *
 * This function provides the UI interface of the profile page, which includes the profile box,
 * recipe page of the user, and the comments of the user.
 *
 * @param navigationActions: NavigationActions object to handle navigation events
 * @param profileViewModel: ProfileViewModel object to interact with profile data
 * @param profileID: Optional profile ID if viewing another user's profile
 */
@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    profileID: String? = null
) {

  var isViewingProfile = false
  if (profileID != null &&
      profileViewModel.googleId != null &&
      profileViewModel.googleId != profileID) {
    profileViewModel.fetchProfile(profileID)
    isViewingProfile = true
  } else if (profileViewModel.googleId != null) {
    profileViewModel.fetchProfile(profileViewModel.googleId)
  } else {
    // Should never occur
    throw Exception(
        "Not Signed-in : No Current FirebaseUser is sign-in. Database isn't accessible if no one is signed-in")
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ProfileScreen"),
      topBar = { TopBarNavigation(title = "Profile") },
      bottomBar = {
        BottomNavigationMenu(Route.PROFILE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        ProfileBox(
            padding,
            profileViewModel.getProfile() ?: throw Exception("No Profile to fetch"),
            navigationActions,
            profileViewModel,
            isViewingProfile)
      })
}

/**
 * A composable function that represents the profile box.
 *
 * This function provides the UI interface of the profile box of the user, which includes the name,
 * username, biography, followers, and following of the user.
 *
 * @param padding: Padding around the profile box depending on the format of the phone
 * @param profile: Extract the needed information from the user's profile in the database
 * @param navigationActions: NavigationActions object to handle navigation events
 * @param isViewingProfile: Flag indicating whether the profile being viewed is the current user's
 *   or not
 */
@Composable
fun ProfileBox(
    padding: PaddingValues,
    profile: Profile,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
    isViewingProfile: Boolean
) { // TODO add font

  Column(
      modifier = Modifier.padding(padding).testTag("ProfileBox"),
      verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              UserProfilePicture()
              Spacer(modifier = Modifier.width(20.dp))
              UserNameBox(profile)
              Spacer(modifier = Modifier.width(5.dp))
              Row(
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically) {
                    FollowersButton(profile, navigationActions)
                    FollowingButton(profile, navigationActions)
                  }
            }
        UserBio(profile)
        ProfileButtons(navigationActions, profile, profileViewModel, isViewingProfile)
      }
}

/** A composable function that generates the user's profile picture. */
@Composable
fun UserProfilePicture() {
  Image(
      modifier = Modifier.width(100.dp).height(100.dp).clip(CircleShape).testTag("ProfileIcon"),
      painter = painterResource(id = R.drawable.user_logo),
      contentDescription = "User Profile Image",
      contentScale = ContentScale.FillBounds)
}

/**
 * A composable function that generates the user's name and username
 *
 * @param profile: extract the needed information from the user's profile in the database
 */
@Composable
fun UserNameBox(profile: Profile) {
  Column(modifier = Modifier.width(100.dp).testTag("ProfileName")) {
    Text(text = profile.name, style = textStyle(17, 15, 700), overflow = TextOverflow.Ellipsis)
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "@" + profile.username,
        style = textStyle(14, 15, 700, TextAlign.Left),
        overflow = TextOverflow.Ellipsis)
  }
}

/**
 * A composable function that generates the user's followers.
 *
 * @param profile: Extract the needed information from the user's profile in the database
 * @param navigationActions: NavigationActions object to handle navigation events
 */
@Composable
fun FollowersButton(profile: Profile, navigationActions: NavigationActions) {
  TextButton(
      modifier = Modifier.testTag("FollowerDisplayButton"),
      onClick = { navigationActions.navigateTo("friends/0") }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(text = "Followers", style = textStyle(10, 20, 600))
              Spacer(modifier = Modifier.height(5.dp))
              Text(text = profile.followers.size.toString(), style = textStyle(10, 30, 600))
            }
      }
}

/**
 * A composable function that generates the user's following.
 *
 * @param profile: Extract the needed information from the user's profile in the database
 * @param navigationActions: NavigationActions object to handle navigation events
 */
@Composable
fun FollowingButton(profile: Profile, navigationActions: NavigationActions) {
  TextButton(
      modifier = Modifier.testTag("FollowingDisplayButton"),
      onClick = { navigationActions.navigateTo("friends/1") }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(text = "Following", style = textStyle(10, 20, 600))
              Spacer(modifier = Modifier.height(5.dp))
              Text(text = profile.following.size.toString(), style = textStyle(10, 30, 600))
            }
      }
}

/**
 * A composable function that generates the user's biography
 *
 * @param profile: extract the needed information from the user's profile in the database
 */
@Composable
fun UserBio(profile: Profile) {
  Text(
      modifier = Modifier.padding(horizontal = 18.dp).testTag("ProfileBio"),
      text = profile.description,
      style = textStyle(13, 15, 400, TextAlign.Justify))
}

/**
 * A composable function that generates the (Edit profile or Follower) and (Share profile) buttons.
 *
 * @param navigationActions: NavigationActions object to handle navigation events
 * @param profile: Extract the needed information from the user's profile in the database
 * @param isViewingProfile: Flag indicating whether the profile being viewed is the current user's
 *   or not
 */
@Composable
fun ProfileButtons(
    navigationActions: NavigationActions,
    profile: Profile,
    profileViewModel: ProfileViewModel,
    isViewingProfile: Boolean
) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        if (!isViewingProfile) {
          OutlinedButton(
              modifier = Modifier.testTag("EditButton"),
              border = BorderStroke(2.dp, FollowButtonBorder),
              onClick = { navigationActions.navigateTo(Screen.EDIT_PROFILE) }) {
                Text(
                    modifier = Modifier.width(110.dp).height(13.dp),
                    text = "Edit Profile",
                    fontWeight = FontWeight.Bold,
                    style = textStyle())
              }
        } else {
          val isFollowing = remember {
            mutableStateOf(profile.followers.contains(profileViewModel.googleId))
          }
          if (isFollowing.value) {
            OutlinedButton(
                colors = ButtonDefaults.buttonColors(containerColor = FollowingButton),
                border = BorderStroke(2.dp, FollowButtonBorder),
                modifier = Modifier.testTag("FollowingButton"),
                onClick = {
                  isFollowing.value = false
                  /*TODO ADD follower*/
                }) {
                  Text(
                      modifier = Modifier.width(110.dp).height(13.dp),
                      text = "Following",
                      fontWeight = FontWeight.Bold,
                      style = textStyle())
                }
          } else {
            OutlinedButton(
                colors = ButtonDefaults.buttonColors(containerColor = FollowButton),
                border = BorderStroke(2.dp, FollowButtonBorder),
                modifier = Modifier.testTag("FollowButton"),
                onClick = {
                  isFollowing.value = true
                  /*TODO REMOVE follower*/
                }) {
                  Text(
                      color = TextBarColor,
                      modifier = Modifier.width(110.dp).height(13.dp),
                      text = "Follow",
                      fontWeight = FontWeight.Bold,
                      style = textStyle(color = TextBarColor))
                }
          }
        }

        OutlinedButton(
            modifier = Modifier.testTag("ShareButton"),
            border = BorderStroke(2.dp, FollowButtonBorder),
            onClick = {
              /*TODO*/
            }) {
              Text(
                  modifier = Modifier.width(110.dp),
                  text = "Share Profile",
                  fontWeight = FontWeight.Bold,
                  style = textStyle())
            }
      }
}

/**
 * A composable helper function that generates the font style for the Text.
 *
 * @param fontSize: Font size of the text (default is 13 sp)
 * @param height: Line height of the text (default is 0 sp, which means automatic line height)
 * @param weight: Font weight of the text (default is 400)
 * @param align: Text alignment (default is TextAlign.Center)
 * @param color: Text color (default is DarkGrey)
 */
@Composable
fun textStyle(
    fontSize: Int = 13,
    height: Int = 0,
    weight: Int = 400,
    align: TextAlign = TextAlign.Center,
    color: Color = DarkGrey
): TextStyle {
  return TextStyle(
      fontSize = fontSize.sp,
      lineHeight = height.sp,
      fontWeight = FontWeight(weight),
      color = DarkGrey,
      textAlign = align)
}

