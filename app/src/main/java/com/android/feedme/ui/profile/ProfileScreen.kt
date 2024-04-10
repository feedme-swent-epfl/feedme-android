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
import androidx.compose.ui.graphics.Color
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
      // topBar = { TODO() },
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
      verticalArrangement = Arrangement.Top
  ) {
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
    Text(
        text = "User Name",
        style =
            TextStyle(
                fontSize = 17.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF191C1E),
                textAlign = TextAlign.Center,
            ))
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = "@username",
        style =
            TextStyle(
                fontSize = 14.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF191C1E),
                textAlign = TextAlign.Center,
            ))
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
              Text(
                  text = "Followers",
                  style =
                      TextStyle(
                          fontSize = 10.sp,
                          lineHeight = 20.sp,
                          fontWeight = FontWeight(600),
                          color = Color(0xFF191C1E),
                          textAlign = TextAlign.Center,
                      ))
              Spacer(modifier = Modifier.height(5.dp))
              Text(
                  text = "0",
                  style =
                      TextStyle(
                          fontSize = 10.sp,
                          lineHeight = 30.sp,
                          fontWeight = FontWeight(600),
                          color = Color(0xFF191C1E),
                          textAlign = TextAlign.Center,
                      ))
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
              Text(
                  text = "Following",
                  style =
                      TextStyle(
                          fontSize = 10.sp,
                          lineHeight = 20.sp,
                          fontWeight = FontWeight(600),
                          color = Color(0xFF191C1E),
                          textAlign = TextAlign.Center,
                      ))
              Spacer(modifier = Modifier.height(5.dp))
              Text(
                  text = "0",
                  style =
                      TextStyle(
                          fontSize = 10.sp,
                          lineHeight = 30.sp,
                          fontWeight = FontWeight(600),
                          color = Color(0xFF191C1E),
                          textAlign = TextAlign.Center,
                      ))
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
      style =
          TextStyle(
              fontSize = 13.sp,
              lineHeight = 15.sp,
              fontWeight = FontWeight(400),
              color = Color(0xFF191C1E),
              textAlign = TextAlign.Justify,
          ))
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
                  style =
                      TextStyle(
                          fontSize = 13.sp,
                          lineHeight = 0.sp,
                          fontWeight = FontWeight(400),
                          color = Color(0xFF191C1E),
                          textAlign = TextAlign.Center,
                      ))
            }
        OutlinedButton(
            modifier = Modifier.testTag("ShareButton"),
            onClick = {
              /*TODO*/
            }) {
              Text(
                  modifier = Modifier.width(110.dp),
                  text = "Share Profile",
                  style =
                      TextStyle(
                          fontSize = 13.sp,
                          lineHeight = 15.sp,
                          fontWeight = FontWeight(400),
                          color = Color(0xFF191C1E),
                          textAlign = TextAlign.Center,
                      ))
            }
      }
}
