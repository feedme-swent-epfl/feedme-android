package com.android.feedme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.android.feedme.resources.C
import com.android.feedme.ui.auth.LoginScreen
import com.android.feedme.ui.camera.CameraScreen
import com.android.feedme.ui.theme.feedmeAppTheme

class MainActivity : ComponentActivity() {
  /**
   * currentScreen and setScreen are used for testing, this is temporary since we still aren't
   * testing Navigation
   */
  // Use mutableStateOf for the currentScreen. Initialize with LOGIN.
  var currentScreen by mutableStateOf(CurrentScreen.LOGIN)
    private set // Make the setter private to control state changes from outside

  // Public method to change the screen, ensuring recomposition
  fun setScreen(screen: CurrentScreen) {
    currentScreen = screen
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      feedmeAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              when (currentScreen) {
                CurrentScreen.LOGIN -> LoginScreen()
                CurrentScreen.CAMERA -> CameraScreen()
              }
            }
      }
    }
  }
}

enum class CurrentScreen {
  LOGIN,
  CAMERA
}
