package com.android.feedme.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.feedme.R
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.TOP_LEVEL_AUTH
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
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
      topBar = { /*TODO: add top bar navigation composable */},
      bottomBar = {
        BottomNavigationMenu(Route.SETTINGS, navigationActions::navigateTo, TOP_LEVEL_DESTINATIONS)
      },
      content = { padding ->
        // TODO: modify the content of the settings screen adapting it to the desired UI screen
        Column {
          Box(
              modifier = Modifier.fillMaxWidth().padding(padding),
              contentAlignment = Alignment.Center) {
                SignOutButton(navigationActions)
              }

          // TODO: modify the content of the settings screen adapting it to the desired UI screen
          Box(
              modifier = Modifier.fillMaxWidth().padding(padding),
              contentAlignment = Alignment.Center) {
                DeleteAccountButton(navigationActions, profileViewModel)
              }
        }
      })
}

/**
 * A composable function representing a sign-out button.
 *
 * @param navigationActions : the nav actions given in the MainActivity
 */
@Composable
fun SignOutButton(navigationActions: NavigationActions) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(context.getString(R.string.default_web_client_id))
          .requestEmail()
          .build()

  val googleSignInClient = GoogleSignIn.getClient(context, gso)

  OutlinedButton(
      // TODO: the button's UI will be updated in a future PR
      modifier = Modifier.testTag("SignOutButton"),
      onClick = { coroutineScope.launch {} }) {
        Text(text = "Sign Out")
      }
}

/**
 * A composable function representing a delete account button.
 *
 * @param navigationActions : the nav actions given in the MainActivity
 */
@Composable
fun DeleteAccountButton(navigationActions: NavigationActions, profileViewModel: ProfileViewModel) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(context.getString(R.string.default_web_client_id))
          .requestEmail()
          .build()

  val googleSignInClient = GoogleSignIn.getClient(context, gso)

  OutlinedButton(
      // TODO: the button's UI will be updated in a future PR
      modifier = Modifier.testTag("DeleteAccountButton"),
      onClick = {
        coroutineScope.launch {
          profileViewModel.deleteCurrentUserProfile(
              {
                Log.d("DeleteAccount", "Account deletion successful")
                // This will sign out the user from Google
                googleSignInClient.signOut().addOnCompleteListener {
                  if (it.isSuccessful) {
                    navigationActions.navigateTo(TOP_LEVEL_AUTH)
                    Log.e("Sign-out", "Sign-out sucessful", it.exception)
                  } else {
                    navigationActions.navigateTo(TOP_LEVEL_AUTH)
                    Log.e("Sign-out", "Sign-out failed", it.exception)
                  }
                }
              },
              { e -> Log.e("DeleteAccount", "Account deletion failed", e) })
        }
      }) {
        Text(text = "Delete Account")
      }
}
