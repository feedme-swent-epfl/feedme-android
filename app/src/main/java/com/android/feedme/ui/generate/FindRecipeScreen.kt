package com.android.feedme.ui.generate

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.android.feedme.model.viewmodel.GenerateViewModel
import com.android.feedme.model.viewmodel.InputViewModel
import com.android.feedme.model.viewmodel.ProfileViewModel
import com.android.feedme.ui.component.IngredientList
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation
import com.android.feedme.ui.theme.CantGenerateColor
import com.android.feedme.ui.theme.DarkGrey
import com.android.feedme.ui.theme.FabColor
import com.android.feedme.ui.theme.GenerateColor
import com.android.feedme.ui.theme.OffWhite
import com.android.feedme.ui.theme.TextBarColor

/**
 * Composable function for the Create Screen.
 *
 * @param navigationActions actions for navigating to different screens.
 */
@Composable
fun FindRecipeScreen(
    navigationActions: NavigationActions,
    inputViewModel: InputViewModel,
    profileViewModel: ProfileViewModel,
    generateViewModel: GenerateViewModel
) {

  val checkMark = remember { mutableStateOf(false) }
  val isStrict = remember { mutableStateOf(true) }
  val dialog = profileViewModel.showDialog.collectAsState()
  val showDialog = remember { mutableStateOf(true) }
  val isComplete by inputViewModel.isComplete.collectAsState()

  if (showDialog.value && dialog.value) {
    Dialog(onDismissRequest = { profileViewModel.setDialog(!checkMark.value) }) {
      Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth().testTag("Dialog"),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                  imageVector = Icons.Default.Info,
                  contentDescription = "Information Icon",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(40.dp).padding(end = 8.dp).testTag("InfoIcon"))

              Text(
                  modifier = Modifier.fillMaxWidth().testTag("InfoText1"),
                  text = "There is a toggle that you can use to generate different recipes.",
                  textAlign = TextAlign.Center)
              Text(
                  modifier = Modifier.fillMaxWidth().testTag("InfoText2"),
                  text =
                      "If you choose strict, the recipe will only include the ingredients you have chosen to input.",
                  textAlign = TextAlign.Center)
              Text(
                  text =
                      "If you choose extra, the recipe will include the ingredients you have inputted and may include additional ingredients.",
                  modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth().testTag("InfoText3"),
                  textAlign = TextAlign.Center)
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(bottom = 16.dp)) {
                    Checkbox(
                        modifier = Modifier.testTag("CheckBox"),
                        checked = checkMark.value,
                        onCheckedChange = { checkMark.value = it })
                    Text("Don't show next time")
                  }
              Text(
                  text = "Dismiss",
                  style =
                      TextStyle(color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                  modifier =
                      Modifier.align(Alignment.CenterHorizontally)
                          .testTag("DismissText")
                          .clickable {
                            profileViewModel.setDialog(!checkMark.value)
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
                    modifier = Modifier.size(27.dp))
              },
              modifier = Modifier.testTag("CameraButton"))

          Spacer(modifier = Modifier.height(10.dp))

          FloatingActionButton(
              containerColor = if (isComplete) GenerateColor else CantGenerateColor,
              contentColor = TextBarColor,
              onClick = {
                checkMark.value = true
                generateViewModel.toggleStrictness(isStrict.value)
                if (profileViewModel.currentUserProfile.value != null) {
                  inputViewModel.isComplete {
                    Log.d("FindRecipeScreen", "Fetching generated recipes")
                    generateViewModel.fetchGeneratedRecipes(
                        inputViewModel.listOfIngredientMetadatas.value.map {
                          it?.ingredient?.id ?: ""
                        },
                        profileViewModel.currentUserProfile.value!!)
                    navigationActions.navigateTo(Screen.GENERATE)
                  }
                }
              },
              content = {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Restaurant Icon",
                    modifier = Modifier.size(27.dp),
                )
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

              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center,
                  modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Strict",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp).testTag("StrictText"))
                    Switch(
                        modifier = Modifier.testTag("ToggleSwitch"),
                        checked = !isStrict.value,
                        onCheckedChange = { isChecked -> isStrict.value = !isChecked },
                        colors =
                            SwitchDefaults.colors(
                                checkedThumbColor = OffWhite, uncheckedThumbColor = DarkGrey))
                    Text(
                        text = "Extra",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp).testTag("ExtraText"))
                  }
              IngredientList(inputViewModel)
            }
      }
}
