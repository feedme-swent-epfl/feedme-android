package com.android.feedme.ui.auth

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.feedme.R
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

/**
 * A composable function representing the login screen.
 *
 * This function provides a UI for users to sign in with Google authentication. It includes a Google
 * sign-in button and handles authentication flow using Firebase authentication.
 *
 * @param navigationActions : the nav actions given in the MainActivity
 */
@Composable
fun LoginScreen(navigationActions: NavigationActions, authViewModel: AuthViewModel = viewModel()) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Configuration for Google Sign-In
  val gso =
      GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestIdToken(context.getString(R.string.default_web_client_id))
          .requestEmail()
          .build()
  val googleSignInClient = GoogleSignIn.getClient(context, gso)

  // Activity Result Launcher for Google Sign-In
  val googleSignInLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult(),
          onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
              val account = task.getResult(ApiException::class.java)
              account?.idToken?.let { idToken ->
                coroutineScope.launch {
                  authViewModel.authenticateWithGoogle(
                      idToken = idToken,
                      onSuccess = {
                        navigationActions.navigateTo(Route.HOME)
                        // Navigate to next screen or show success message
                      },
                      onFailure = { exception ->
                        // Log error or show error message
                        Log.e("LoginScreen", "Authentication failed", exception)
                      })
                }
              }
            } catch (e: ApiException) {
              // Handle API exception
              Log.e("LoginScreen", "Sign in failed", e)
            }
          })

  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp).testTag("LoginScreen"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.sign_in_logo),
            contentDescription = "Sign-in Logo",
            modifier = Modifier.width(189.dp).height(189.dp),
            contentScale = ContentScale.FillBounds)

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome",
            modifier = Modifier.testTag("LoginTitle"),
            // M3/display/large
            style =
                TextStyle(
                    fontSize = 57.sp,
                    lineHeight = 64.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF191C1E),
                    textAlign = TextAlign.Center,
                ))

        Spacer(modifier = Modifier.height(176.dp))
        Button(
            onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
            modifier =
                Modifier.width(250.dp)
                    .height(40.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 20.dp))
                    .testTag("LoginButton"),
            colors = ButtonDefaults.buttonColors(Color.White),
            contentPadding = PaddingValues(2.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFFDADCE0))) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Replace R.drawable.ic_google_logo with the actual resource ID for
                    // the Google logo
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = null, // Provide a meaningful content description
                        modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        modifier = Modifier.width(125.dp).height(17.dp),
                        text = "Sign In with Google",
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF3C4043),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.25.sp,
                    )
                  }
            }
      }
}
