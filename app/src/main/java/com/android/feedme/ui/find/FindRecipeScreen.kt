package com.android.feedme.ui.find

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.android.feedme.R
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.ui.component.IngredientList
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.FabColor
import com.android.feedme.ui.theme.TextBarColor

/**
 * Composable function for the Create Screen.
 *
 * @param navigationActions actions for navigating to different screens.
 */
@Composable
fun FindRecipeScreen(navigationActions: NavigationActions, inputViewModel: InputViewModel) {

  val showDialog = remember { mutableStateOf(false) }

  if (showDialog.value) {
    Dialog(onDismissRequest = { showDialog.value = false }) {
      Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = "Information Icon",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(40.dp).padding(end = 8.dp))

              Text(
                  modifier = Modifier.fillMaxWidth(),
                  text = "Please validate how you want to generate the recipes.",
                  textAlign = TextAlign.Center)
              Text(
                  modifier = Modifier.fillMaxWidth(),
                  text =
                      "If you choose strict, the recipe will only include the ingredients you have chosen to input.",
                  textAlign = TextAlign.Center)
              Text(
                  text =
                      "If you choose extra, the recipe will include the ingredients you have inputted and may include additional ingredients.",
                  modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
                  textAlign = TextAlign.Center)
              Row(
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /* Handle strict ingredients validation */},
                        modifier = Modifier.weight(1f)) {
                          Text(
                              text = "Strict",
                              style =
                                  TextStyle(
                                      color = TextBarColor,
                                      fontSize = 18.sp,
                                  ))
                        }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /* Handle extra ingredients validation */},
                        modifier = Modifier.weight(1f)) {
                          Text(
                              text = "Extra",
                              style =
                                  TextStyle(
                                      color = TextBarColor,
                                      fontSize = 18.sp,
                                  ))
                        }
                  }
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                  text = "Cancel",
                  style =
                      TextStyle(
                          color = Color.Red,
                          fontSize = 14.sp,
                          fontWeight = FontWeight.Bold,
                          textDecoration = TextDecoration.Underline),
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally).clickable {
                        showDialog.value = false
                      })
            }
      }
    }
  }

  Scaffold(
      modifier = Modifier.testTag("FindRecipeScreen"),
      topBar = { TopBarNavigation(title = "Generate Recipe") },
      floatingActionButton = {
        Column {
          FloatingActionButton(
              containerColor = FabColor,
              contentColor = TextBarColor,
              onClick = { navigationActions.navigateTo(Screen.CAMERA) },
              content = {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(24.dp))
              },
              modifier = Modifier.testTag("CameraButton"))

          Spacer(modifier = Modifier.height(10.dp))

          FloatingActionButton(
              containerColor = FabColor,
              contentColor = TextBarColor,
              onClick = { showDialog.value = true },
              content = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(24.dp))
              },
              modifier = Modifier.testTag("ValidateButton"))
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            selectedItem = Route.FIND_RECIPE,
            onTabSelect = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS)
      }) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(top = 10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {

              // Title
              Text(
                  text = "Ingredients",
                  style = MaterialTheme.typography.headlineMedium,
                  fontWeight = FontWeight.Bold,
              )

              // Line separator
              Image(
                  modifier =
                      Modifier.border(width = 4.dp, color = Color.Gray)
                          .padding(4.dp)
                          .width(180.dp)
                          .height(0.dp),
                  painter = painterResource(id = R.drawable.line_8),
                  contentDescription = "Line Separator",
                  contentScale = ContentScale.None)

              // List Of Ingredients
              IngredientList(inputViewModel)
            }
      }
}
