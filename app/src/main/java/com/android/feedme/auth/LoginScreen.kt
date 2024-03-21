package com.android.feedme.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen() {
  // TODO : Add an argument for the navigation as in bootcamp : navAction: NavigationActions

  Firebase.auth.signOut()

  @Composable
  fun rememberFirebaseAuthLauncher(
      onAuthComplete: (AuthResult) -> Unit,
      onAuthError: (ApiException) -> Unit
  ): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
      val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
      try {
        val account = task.getResult(ApiException::class.java)!!
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        scope.launch {
          val authResult = Firebase.auth.signInWithCredential(credential).await()
          onAuthComplete(authResult)
        }
      } catch (e: ApiException) {
        onAuthError(e)
      }
    }
  }

  var user by remember { mutableStateOf(Firebase.auth.currentUser) }
  val launcher =
      rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            user = result.user
            // TODO ADD NAVIGATION HERE
          },
          onAuthError = { user = null })
  val token = stringResource(R.string.web_client_id)
  val context = LocalContext.current

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
        // You need to implement launching the sign-in activity here
        Button(
            onClick = {
              val gso =
                  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                      .requestIdToken(token)
                      .requestEmail()
                      .build()
              val googleSignInClient = GoogleSignIn.getClient(context, gso)
              launcher.launch(googleSignInClient.signInIntent)
            },
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
