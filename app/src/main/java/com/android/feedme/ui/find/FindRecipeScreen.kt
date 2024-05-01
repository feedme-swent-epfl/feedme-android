package com.android.feedme.ui.find

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.feedme.R
import com.android.feedme.ui.component.IngredientList
import com.android.feedme.ui.navigation.BottomNavigationMenu
import com.android.feedme.ui.navigation.NavigationActions
import com.android.feedme.ui.navigation.Route
import com.android.feedme.ui.navigation.Screen
import com.android.feedme.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.android.feedme.ui.navigation.TopBarNavigation

/**
 * Composable function for the Create Screen.
 *
 * @param navigationActions actions for navigating to different screens.
 */
@Composable
fun FindRecipeScreen(navigationActions: NavigationActions) {

  Scaffold(
      modifier = Modifier.testTag("FindRecipeScreen"),
      topBar = { TopBarNavigation(title = "Find Recipe") },
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

              // Camera Button
              OutlinedButton(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 20.dp)
                          .padding(bottom = 20.dp)
                          .testTag("CameraButton"),
                  shape = RoundedCornerShape(size = 10.dp),
                  onClick = { navigationActions.navigateTo(Screen.CAMERA) },
                  border = BorderStroke(width = 2.dp, color = Color.Black)) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Add Icon",
                        tint = Color(0xFF4E5FFB),
                        modifier = Modifier.size(24.dp))

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Scan with Camera",
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF4E5FFB),
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.25.sp,
                            ))
                  }

              // Gallery Button
              OutlinedButton(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = 20.dp)
                          .padding(bottom = 20.dp)
                          .testTag("GalleryButton"),
                  shape = RoundedCornerShape(size = 10.dp),
                  onClick = { navigationActions.navigateTo(Screen.GALLERY) },
                  border = BorderStroke(width = 2.dp, color = Color.Black)) {
                    Icon(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Add Icon",
                        tint = Color(0xFF4E5FFB),
                        modifier = Modifier.size(24.dp))

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Select from Gallery",
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight(700),
                                color = Color(0xFF4E5FFB),
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.25.sp,
                            ))
                  }

              // Line separator
              Image(
                  modifier =
                      Modifier.border(width = 4.dp, color = Color(0xFF8C8C8C))
                          .padding(4.dp)
                          .width(180.dp)
                          .height(0.dp),
                  painter = painterResource(id = R.drawable.line_8),
                  contentDescription = "Line Separator",
                  contentScale = ContentScale.None)

              // List Of Ingredients
              IngredientList()
            }
      }
}