package com.android.feedme.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.BackgroundColor
import com.android.feedme.ui.theme.Cyan
import com.android.feedme.ui.theme.OffWhite

/**
 * A composable function that generates the welcome screen.
 *
 * This function provides the UI interface of the welcome page, which includes the app logo, small
 * welcome text and a button to edit the profile
 *
 * @param navigationActions: NavigationActions object to handle navigation events
 */
@Composable
fun WelcomeScreen(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  Scaffold(
      modifier = Modifier.fillMaxSize(), // .testTag("CreateProfileScreen"),   //TODO create tests
      topBar = { TopBarNavigation(title = "FeedMe") },
      bottomBar = {
        BottomNavigationMenu(
            Route.PROFILE, { top -> navigationActions.navigateTo(top) }, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding -> WelcomeScreenContent(padding, profileViewModel, navigationActions) })
}

@Composable
fun WelcomeScreenContent(
    padding: PaddingValues,
    profileViewModel: ProfileViewModel,
    navigationActions: NavigationActions
) {
  val density = LocalDensity.current
  val arcHeight = 50.dp
  val arcHeightPx = with(density) { arcHeight.toPx() }

  // Shape for the white arc at the top
  val topArcShape = GenericShape { size, _ ->
    moveTo(0f, arcHeightPx)
    quadraticBezierTo(size.width / 2f, -arcHeightPx, size.width, arcHeightPx)
    lineTo(size.width, size.height)
    lineTo(0f, size.height)
    close()
  }

  Box(modifier = Modifier.fillMaxSize().background(Color(0xFF003C66)).padding(padding)) {
    // White area with top arc
    Box(
        modifier =
            Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(600.dp)
                .clip(topArcShape)
                .background(OffWhite)
                .padding(horizontal = 32.dp, vertical = 50.dp)) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.align(Alignment.TopCenter)) {
                Spacer(modifier = Modifier.height(40.dp)) // Space for the logo outside this box
                Text(
                    textAlign = TextAlign.Center,
                    text = "Welcome to FeedMe!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight(600),
                    color = BackgroundColor)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Looks like you don’t have an account, click below to create one",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600),
                    color = BackgroundColor,
                    textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {
                      profileViewModel.fetchCurrentUserProfile()
                      navigationActions.navigateTo(Screen.EDIT_PROFILE)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    border = BorderStroke(2.dp, Cyan)) {
                      Text(text = "Create account", fontSize = 18.sp, color = BackgroundColor)
                    }
              }
        }
    // Logo placement
    Image(
        painter = painterResource(id = R.drawable.sign_in_logo),
        contentDescription = "Logo",
        modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp).size(100.dp))
  }
}