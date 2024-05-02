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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import com.android.feedme.model.data.Ingredient
import com.android.feedme.model.data.IngredientMetaData
import com.android.feedme.model.data.MeasureUnit
import com.android.feedme.model.data.Profile
import com.android.feedme.model.data.Recipe
import com.android.feedme.model.data.Step
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.component.SmallThumbnailsDisplay
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
 */
@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel,
) {

  val profile =
      if (profileViewModel.isViewingProfile()) profileViewModel.viewingUserProfile.collectAsState()
      else profileViewModel.currentUserProfile.collectAsState()

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("ProfileScreen"),
      topBar = {
        TopBarNavigation(
            title = "Profile",
            navAction = if (profileViewModel.viewingUserId == null) null else navigationActions,
            backArrowOnClickAction = {
              profileViewModel.removeViewingProfile()
              navigationActions.goBack()
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            Route.PROFILE,
            { top ->
              profileViewModel.removeViewingProfile()
              navigationActions.navigateTo(top)
            },
            TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        ProfileBox(padding, profile.value, navigationActions, profileViewModel)
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
 * @param profileViewModel: ProfileViewModel object to interact with profile data
 */
@Composable
fun ProfileBox(
    padding: PaddingValues,
    profile: Profile?,
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel
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
              UserNameBox(profile ?: Profile())
              Spacer(modifier = Modifier.width(5.dp))
              Row(
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically) {
                    FollowersButton(profile ?: Profile(), navigationActions)
                    FollowingButton(profile ?: Profile(), navigationActions)
                  }
            }
        UserBio(
            profile ?: Profile(),
        )
        ProfileButtons(navigationActions, profile ?: Profile(), profileViewModel)
        val recipe =
            Recipe(
                recipeId = "lasagna1",
                title = "Tasty Lasagna",
                description =
                    "Description of the recipe, writing a longer one to see if it fills up the whole space available. Still writing with no particular aim lol",
                ingredients =
                    listOf(
                        IngredientMetaData(
                            quantity = 2.0,
                            measure = MeasureUnit.ML,
                            ingredient = Ingredient("Tomato", "Vegetables", "tomatoID"))),
                steps =
                    listOf(
                        Step(
                            1,
                            "In a large, heavy pot, put the olive oil, garlic and parsley over medium high heat. When the garlic begins to brown, increase the heat and add the ground beef. Break up the beef, but keep it rather chunky. Sprinkle with about 1/2 tsp of salt. \n" +
                                "\n" +
                                "When the beef is beginning to dry up, add the tomatoes and stir well. Add more salt, then lower the heat and allow to simmer for about an hour, stirring from time to time. Taste for salt and add pepper.",
                            "Make the Meat Sauce")),
                tags = listOf("Meat"),
                time = 45.0,
                rating = 4.5,
                userid = "username",
                difficulty = "Intermediate",
                "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fwww.mamablip.com%2Fstorage%2FLasagna%2520with%2520Meat%2520and%2520Tomato%2520Sauce_3481612355355.jpg&f=1&nofb=1&ipt=8e887ba99ce20a85fb867dabbe0206c1146ebf2f13548b5653a2778e3ea18c54&ipo=images")

        SmallThumbnailsDisplay(listOf(recipe, recipe, recipe, recipe))
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
    profileViewModel: ProfileViewModel
) {
  val isFollowing = remember {
    mutableStateOf(profile.followers.contains(profileViewModel.currentUserId))
  }

  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        if (!profileViewModel.isViewingProfile()) {
          EditProfileButton(navigationActions)
        } else {
          FollowUnfollowButton(profile, isFollowing, profileViewModel)
        }
        ShareProfileButton()
      }
}

/**
 * A composable function that generates the edit profile button.
 *
 * @param navigationActions: NavigationActions object to handle navigation events
 */
@Composable
fun EditProfileButton(navigationActions: NavigationActions) {
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
}

/**
 * A composable function that generates the follow/unfollow button.
 *
 * @param profile: Extract the needed information from the user's profile in the database
 * @param isFollowing: Flag indicating whether the current user is following the profile or not
 * @param profileViewModel: ProfileViewModel object to interact with profile data
 */
@Composable
fun FollowUnfollowButton(
    profile: Profile,
    isFollowing: MutableState<Boolean>,
    profileViewModel: ProfileViewModel,
) {
  if (isFollowing.value) {
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(containerColor = FollowingButton),
        border = BorderStroke(2.dp, FollowButtonBorder),
        modifier = Modifier.testTag("FollowingButton"),
        onClick = {
          // Unfollow logic
          isFollowing.value = false
          profileViewModel.unfollowUser(profile)
        }) {
          Text(
              modifier = Modifier.width(110.dp).height(13.dp),
              text = "Unfollow",
              fontWeight = FontWeight.Bold,
              style = textStyle())
        }
  } else {
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(containerColor = FollowButton),
        border = BorderStroke(2.dp, FollowButtonBorder),
        modifier = Modifier.testTag("FollowButton"),
        onClick = {
          // Follow logic
          isFollowing.value = true
          profileViewModel.followUser(profile) // Assuming the function signature matches
        }) {
          Text(
              modifier = Modifier.width(110.dp).height(13.dp),
              text = "Follow",
              color = TextBarColor,
              fontWeight = FontWeight.Bold,
              style = textStyle(color = TextBarColor))
        }
  }
}

/** A composable function that generates the share profile button. */
@Composable
fun ShareProfileButton() {
  OutlinedButton(
      modifier = Modifier.testTag("ShareButton"),
      border = BorderStroke(2.dp, FollowButtonBorder),
      onClick = {
        // Placeholder for future share functionality
      }) {
        Text(
            modifier = Modifier.width(110.dp),
            text = "Share Profile",
            fontWeight = FontWeight.Bold,
            style = textStyle())
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
