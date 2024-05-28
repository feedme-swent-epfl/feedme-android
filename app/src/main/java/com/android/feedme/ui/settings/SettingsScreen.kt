package com.android.feedme.ui.settings

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_AUTH
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.profile.UserProfilePicture
import com.android.feedme.ui.theme.BlueUsername
import com.android.feedme.ui.theme.TemplateColor
import com.android.feedme.ui.theme.deleteButtonColor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

/**
 * A composable function representing the settings screen.
 *
 * This function provides a UI for users to adjust settings in the app.
 *
 * @param navigationActions : the nav actions given in the MainActivity
 */
@Composable
fun SettingsScreen(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  Scaffold(
      modifier = Modifier.testTag("SettingsScreen"),
      topBar = { TopBarNavigation(title = "Settings") },
      bottomBar = {
        BottomNavigationMenu(Route.SETTINGS, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        // TODO: modify the content of the settings screen adapting it to the desired UI screen
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          SettingsPage(navigationActions, profileViewModel)
        }
      })
}

/**
 * Composable function to display the settings page
 *
 * @param navigationActions : the [NavigationActions] given in the MainActivity
 */
@Composable
fun SettingsPage(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(context.getString(R.string.default_web_client_id))
          .requestEmail()
          .build()

  val googleSignInClient = GoogleSignIn.getClient(context, gso)

  var showDialog by remember { mutableStateOf(false) }

  Column(
      modifier = Modifier.fillMaxSize().background(Color.White),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Center) {
        Box(
            modifier = Modifier.height(200.dp).width(300.dp)
            /*.border(2.dp, TemplateColor, RoundedCornerShape(20.dp))*/ ) {
              Column(
                  verticalArrangement = Arrangement.Top,
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.fillMaxSize()) {
                    // Profile icon
                    UserProfilePicture(
                        profileViewModel,
                        Modifier.width(125.dp)
                            .border(2.dp, Color.LightGray, CircleShape)
                            .height(125.dp))

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text =
                            buildAnnotatedString {
                              withStyle(
                                  style =
                                      SpanStyle(
                                          fontWeight = FontWeight.Medium, color = BlueUsername)) {
                                    append(
                                        "@${profileViewModel.currentUserProfile.collectAsState().value?.username}")
                                  }
                            },
                        modifier =
                            Modifier.align(alignment = Alignment.CenterHorizontally)
                                .testTag("Username"))
                  }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign out button
        OutlinedButton(
            onClick = {
              coroutineScope.launch {
                // This will sign out the user from Google
                googleSignInClient.signOut().addOnCompleteListener {
                  if (it.isSuccessful) {
                    navigationActions.navigateTo(TOP_LEVEL_AUTH)
                    Log.d("SignOut", "Sign out successful")
                  } else {
                    // Handle the error, could not sign out
                    Log.e("SignOut", "Sign out failed", it.exception)
                  }
                }
              }
            },
            colors = ButtonDefaults.buttonColors(Color.White),
            border = BorderStroke(2.dp, TemplateColor),
            modifier = Modifier.width(300.dp).height(48.dp).testTag("SignOutButton"),
            shape = RoundedCornerShape(20.dp)) {
              Text(
                  "Sign Out",
                  color = TemplateColor,
                  fontWeight = FontWeight.Medium,
                  fontSize = 16.sp)
            }
        Spacer(modifier = Modifier.height(16.dp))

        // Delete account button
        OutlinedButton(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(Color.White),
            border = BorderStroke(2.dp, deleteButtonColor),
            modifier = Modifier.width(300.dp).height(48.dp).testTag("DeleteAccountButton"),
            shape = RoundedCornerShape(20.dp)) {
              Text(
                  "Delete Account",
                  color = deleteButtonColor,
                  fontWeight = FontWeight.Medium,
                  fontSize = 16.sp)
            }
        // Confirmation dialog
        if (showDialog) {
          AlertDialog(
              modifier = Modifier.testTag("AlertDialogBox"),
              onDismissRequest = {
                // Dismiss the dialog if the user cancels
                showDialog = false
              },
              title = { Text(text = "Confirm Deletion") },
              text = { Text(text = "Are you sure you want to delete your account?") },
              confirmButton = {
                Button(
                    modifier = Modifier.testTag("ConfirmButton"),
                    onClick = {
                      showDialog = false
                      // Proceed with account deletion
                      profileViewModel.fetchCurrentUserProfile()
                      coroutineScope.launch {
                        profileViewModel.deleteCurrentUserProfile(
                            {
                              Log.d("DeleteAccount", "Account deletion successful")
                              googleSignInClient.signOut().addOnCompleteListener {
                                if (it.isSuccessful) {
                                  navigationActions.navigateTo(TOP_LEVEL_AUTH)
                                  Log.d("Sign-out", "Sign-out successful")
                                } else {
                                  navigationActions.navigateTo(TOP_LEVEL_AUTH)
                                  Log.e("Sign-out", "Sign-out failed", it.exception)
                                }
                              }
                            },
                            { e -> Log.e("DeleteAccount", "Account deletion failed", e) })
                      }
                    }) {
                      Text(text = "Confirm")
                    }
              },
              dismissButton = {
                Button(
                    modifier = Modifier.testTag("DismissButton"),
                    onClick = {
                      // Dismiss the dialog if the user cancels
                      showDialog = false
                    }) {
                      Text(text = "Cancel")
                    }
              })
        }
      }
}
