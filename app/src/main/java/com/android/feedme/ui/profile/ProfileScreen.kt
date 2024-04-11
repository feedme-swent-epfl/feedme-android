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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R
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
fun ProfileScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ProfileScreen"),
      topBar = { TopBarNavigation(title = "Profile") },
      bottomBar = {
        BottomNavigationMenu(Route.PROFILE, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding -> ProfileBox(padding) })
}

/**
 * A composable function that represents the profile box
 *
 * This function provides the UI interface of the profile box of the user, which includes the name,
 * username, biography, followers and following of the user.
 */
@Composable
fun ProfileBox(padding: PaddingValues) { // TODO add font
  Column(
      modifier = Modifier.padding(padding).testTag("ProfileBox"),
      verticalArrangement = Arrangement.Top) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              UserProfilePicture()
              Spacer(modifier = Modifier.width(20.dp))
              UserNameBox()
              Spacer(modifier = Modifier.width(5.dp))
              Row(
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically) {
                    FollowersButton()
                    FollowingButton()
                  }
            }
        UserBio()
        ProfileButtons()
      }
}

/** A composable function that generates the user's profile picture */
@Composable
fun UserProfilePicture() {
  Image(
      modifier = Modifier.width(100.dp).height(100.dp).clip(CircleShape).testTag("ProfileIcon"),
      painter = painterResource(id = R.drawable.user_logo),
      contentDescription = "User Profile Image",
      contentScale = ContentScale.FillBounds)
}

/** A composable function that generates the user's name and username */
@Composable
fun UserNameBox() {
  Column(modifier = Modifier.width(100.dp).testTag("ProfileName")) {
    Text(text = "User Name", style = textStyle(17, 15, 700, TextAlign.Center))
    Spacer(modifier = Modifier.height(10.dp))
    Text(text = "@username", style = textStyle(14, 15, 700, TextAlign.Center))
  }
}

/** A composable function that generates the user's followers */
@Composable
fun FollowersButton() {
  TextButton(
      modifier = Modifier.testTag("FollowerButton"),
      onClick = {
        /*TODO*/
      }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(text = "Followers", style = textStyle(10, 20, 600, TextAlign.Center))
              Spacer(modifier = Modifier.height(5.dp))
              Text(text = "0", style = textStyle(10, 30, 600, TextAlign.Center))
            }
      }
}

/** A composable function that generates the user's following */
@Composable
fun FollowingButton() {
  TextButton(
      modifier = Modifier.testTag("FollowingButton"),
      onClick = {
        /*TODO*/
      }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(text = "Following", style = textStyle(10, 20, 600, TextAlign.Center))
              Spacer(modifier = Modifier.height(5.dp))
              Text(text = "0", style = textStyle(10, 30, 600, TextAlign.Center))
            }
      }
}

/** A composable function that generates the user's biography */
@Composable
fun UserBio() {
  Text(
      modifier = Modifier.padding(horizontal = 18.dp).testTag("ProfileBio"),
      text =
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed. And the oceans we pigs.",
      style = textStyle(13, 15, 400, TextAlign.Justify))
}

/** A composable function that generates the Edit profile and Share profile buttons */
@Composable
fun ProfileButtons() {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            modifier = Modifier.testTag("EditButton"),
            onClick = {
              /*TODO*/
            }) {
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

@Composable
fun textStyle(fontSize: Int, height: Int, weight: Int, align: TextAlign): TextStyle {
  return TextStyle(
      fontSize = fontSize.sp,
      lineHeight = height.sp,
      fontWeight = FontWeight(weight),
      color = DarkGrey,
      textAlign = align)
}
