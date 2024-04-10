package com.android.feedme.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R

/**
 * A composable function that generates the profile screen
 *
 * This function provides the UI interface of the profile page, which includes the profile box,
 * recipe page of the user and the comments of the user.
 */
@Composable
fun ProfileScreen() {
    ProfileBox() //TODO add top and bottom bar and navigation menu
}

/**
 * A composable function that represents the profile box
 *
 * This function provides the UI interface of the profile box of the user, which includes the name,
 * username, biography, followers and following of the user.
 */
@Composable
fun ProfileBox() { //TODO add font
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfilePicture()
            Spacer(modifier = Modifier.width(20.dp))
            UserNameBox()
            Spacer(modifier = Modifier.width(5.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FollowersButton()
                FollowingButton()
            }
        }
        UserBio()
        ProfileButtons()
    }
}

/**
 * A composable function that generates the user's profile picture
 */
@Composable
fun UserProfilePicture() {
    Image(
        painter = painterResource(id = R.drawable.user_logo),
        contentDescription = "User Profile Image",
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clip(CircleShape),
        contentScale = ContentScale.FillBounds
    )
}

/**
 * A composable function that generates the user's name and username
 */
@Composable
fun UserNameBox() {
    Column(
        modifier = Modifier.width(100.dp)
    ) {
        Text(
            text = "User Name",
            style =
            TextStyle(
                fontSize = 17.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF191C1E),
                textAlign = TextAlign.Center,
            )
        )
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
            )
        )
    }
}

/**
 * A composable function that generates the user's followers
 */
@Composable
fun FollowersButton() {
    TextButton(
        onClick = {
            /*TODO*/
        })
    {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                text = "Followers",
                style =
                TextStyle(
                    fontSize = 10.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF191C1E),
                    textAlign = TextAlign.Center,
                )
            )
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
                )
            )
        }
    }
}

/**
 * A composable function that generates the user's following
 */
@Composable
fun FollowingButton() {
    TextButton(
        onClick = {
            /*TODO*/
        })
    {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                text = "Following",
                style =
                TextStyle(
                    fontSize = 10.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF191C1E),
                    textAlign = TextAlign.Center,
                )
            )
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
                )
            )
        }
    }
}

/**
 * A composable function that generates the user's biography
 */
@Composable
fun UserBio() {
    Text(
        modifier = Modifier.padding(horizontal = 18.dp),
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed. And the oceans we pigs.",
        style = TextStyle(
            fontSize = 13.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFF191C1E),
            textAlign = TextAlign.Justify,
        )
    )
}

/**
 * A composable function that generates the Edit profile and Share profile buttons
 */
@Composable
fun ProfileButtons() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = {
            /*TODO*/
        }) {
            Text(
                modifier = Modifier
                    .width(110.dp)
                    .height(13.dp),
                text = "Edit Profile",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 0.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF191C1E),
                    textAlign = TextAlign.Center,
                )
            )
        }
        OutlinedButton(onClick = {
            /*TODO*/
        }) {
            Text(
                modifier = Modifier.width(110.dp),
                text = "Share Profile",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF191C1E),
                    textAlign = TextAlign.Center,
                )
            )
        }
    }
}