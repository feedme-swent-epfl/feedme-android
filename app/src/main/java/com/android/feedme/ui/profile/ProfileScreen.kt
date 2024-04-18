package com.android.feedme.ui.profile

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.DarkGrey

/**
 * A composable function that generates the profile screen
 *
 * This function provides the UI interface of the profile page, which includes the profile box,
 * recipe page of the user and the comments of the user.
 */
@Composable
fun ProfileScreen(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  val profile = profileViewModel.profile.collectAsState().value

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ProfileScreen"),
      topBar = { TopBarNavigation(title = "Profile") },
      bottomBar = {
        BottomNavigationMenu(Route.PROFILE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        ProfileBox(
            padding,
            profileViewModel.profile.collectAsState().value ?: Profile(),
            navigationActions)
      })
}

/**
 * A composable function that represents the profile box
 *
 * This function provides the UI interface of the profile box of the user, which includes the name,
 * username, biography, followers and following of the user.
 *
 * @param padding: pad around the profile box depending on the format of the phone
 * @param profile: extract the needed information from the user's profile in the database
 */
@Composable
fun ProfileBox(
    padding: PaddingValues,
    profile: Profile,
    navigationActions: NavigationActions
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
        ProfileButtons(navigationActions)
      }
}

/**
 * A composable function that generates the user's profile picture
 *
 * @param profile: extract the needed information from the user's profile in the database
 */
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
    Text(
        text = profile.name,
        style = textStyle(17, 15, 700, TextAlign.Center),
        overflow = TextOverflow.Ellipsis)
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "@" + profile.username,
        style = textStyle(14, 15, 700, TextAlign.Center),
        overflow = TextOverflow.Ellipsis)
  }
}

/**
 * A composable function that generates the user's followers
 *
 * @param profile: extract the needed information from the user's profile in the database
 */
@Composable
fun FollowersButton(profile: Profile, navigationActions: NavigationActions) {
  TextButton(
      modifier = Modifier.testTag("FollowerButton"),
      onClick = { navigationActions.navigateTo("friends/0") }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(text = "Followers", style = textStyle(10, 20, 600, TextAlign.Center))
              Spacer(modifier = Modifier.height(5.dp))
              Text(
                  text = profile.followers.size.toString(),
                  style = textStyle(10, 30, 600, TextAlign.Center))
            }
      }
}

/**
 * A composable function that generates the user's following
 *
 * @param profile: extract the needed information from the user's profile in the database
 */
@Composable
fun FollowingButton(profile: Profile, navigationActions: NavigationActions) {
  TextButton(
      modifier = Modifier.testTag("FollowingButton"),
      onClick = { navigationActions.navigateTo("friends/1") }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(text = "Following", style = textStyle(10, 20, 600, TextAlign.Center))
              Spacer(modifier = Modifier.height(5.dp))
              Text(
                  text = profile.following.size.toString(),
                  style = textStyle(10, 30, 600, TextAlign.Center))
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

/** A composable function that generates the Edit profile and Share profile buttons */
@Composable
fun ProfileButtons(navigationActions: NavigationActions) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            modifier = Modifier.testTag("EditButton"),
            onClick = { navigationActions.navigateTo(Route.EDITPROFILE) }) {
              Text(
                  modifier = Modifier.width(110.dp).height(13.dp),
                  text = "Edit Profile",
                  style = textStyle(13, 0, 400, TextAlign.Center))
            }
        OutlinedButton(
            modifier = Modifier.testTag("ShareButton"),
            onClick = {
              /*TODO*/
            }) {
              Text(
                  modifier = Modifier.width(110.dp),
                  text = "Share Profile",
                  style = textStyle(13, 0, 400, TextAlign.Center))
            }
      }
}

/** A composable helper function that generates the font style for the Text */
@Composable
fun textStyle(fontSize: Int, height: Int, weight: Int, align: TextAlign): TextStyle {
  return TextStyle(
      fontSize = fontSize.sp,
      lineHeight = height.sp,
      fontWeight = FontWeight(weight),
      color = DarkGrey,
      textAlign = align)
}
